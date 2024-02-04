package services.visionTracker

import model.enums.Team
import services.entityPropertyGetter.getEntityProperty
import services.entityPropertyGetter.getPosition
import services.entityUpdateProvider.EntityUpdateProvider
import services.heroComponentFactory.HeroComponentFactory
import services.stringTableProvider.StringTableProvider
import services.ticker.ITicker
import skadistats.clarity.model.Entity

class VisionTracker(
    private val heroComponentFactory: HeroComponentFactory,
    private val ticker: ITicker,
    private val entitiesProvider: EntityUpdateProvider,
    private val stringTableProvider: StringTableProvider
) {
    private val secondEventListener = { time: Int -> this.onSecond(time) }

    init {
        ticker.subscribeToSecond(secondEventListener)
    }

    fun dispose() {
        ticker.unsubscribeFromSecond(secondEventListener)
    }

    //todo: add invisible units
    private fun onSecond(time: Int) {
        val entities = entitiesProvider.entities ?: return
        for (entity in entities.getAllByPredicate { e -> isEntityProvidingVision(e) }) {
            val team = Team.fromInt(entity.getEntityProperty<Int>("m_iTeamNum") ?: -1)
            val position = entity.getPosition()

            val dayVision = entity.getEntityProperty<Int>("m_iDayTimeVisionRange") ?: 0
            val nightVision = entity.getEntityProperty<Int>("m_iNightTimeVisionRange") ?: 0
            val vision = if (isDayTime()) dayVision else nightVision

            val visionRange = vision.toFloat()
            val visionRangeSquared = visionRange * visionRange

            for (heroComponent in heroComponentFactory.retrieveHeroComponents()) {
                val otherTeam = heroComponent.heroModel.team
                if (team == otherTeam) continue
                val otherPosition = heroComponent.heroEntity.getPosition()
                if (position.z < otherPosition.z) continue
                val distanceSquared = position.sqrDistance(otherPosition)
                if (distanceSquared <= visionRangeSquared) {
                    heroComponent.markSeen()
                }
            }
        }
    }

    private fun isEntityProvidingVision(entity: Entity): Boolean {
        val teamInt = entity.getEntityProperty<Int>("m_iTeamNum") ?: return false
        val dayVision = entity.getEntityProperty<Int>("m_iDayTimeVisionRange") ?: return false
        val nightVision = entity.getEntityProperty<Int>("m_iNightTimeVisionRange") ?: return false
        val team = Team.fromInt(teamInt)

        return (team == Team.RADIANT || team == Team.DIRE) && (dayVision > 0 || nightVision > 0)
    }

    private fun isDayTime(): Boolean {
        val timeOfDay = entitiesProvider.entities?.getByDtName("CDOTAGamerulesProxy")
            ?.getEntityProperty<Boolean>("m_pGameRules.m_bIsTemporaryDay") ?: return false
        return timeOfDay
    }
}