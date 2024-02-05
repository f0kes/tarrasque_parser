package services.entityProvider

import services.Disposable
import services.entityPropertyGetter.getEntityProperty
import services.entityUpdateProvider.EntityUpdate
import services.entityUpdateProvider.EntityUpdateProvider
import services.ticker.ITicker
import skadistats.clarity.model.Entity


//faster but less reliable
class HeroEntitiesProviderByPlayerResource(
    private val entityUpdateProvider: EntityUpdateProvider,
    private val ticker: ITicker
) : HeroEntitiesProvider {

    private val heroes: MutableSet<Entity> = mutableSetOf()
    private val entityUpdateEventListener = { upd: EntityUpdate -> onEntityUpdate(upd) }
    private var isDirty: Boolean = true

    init {
        entityUpdateProvider.subscribe(entityUpdateEventListener)
    }

    override fun dispose() {
        entityUpdateProvider.unsubscribe(entityUpdateEventListener)
    }

    override fun retrieveEntities(): List<Entity> {
        if (isDirty) {
            updateHeroes()
            isDirty = false
        }
        return heroes.toList()
    }

    private fun updateHeroes() {
        val entities = entityUpdateProvider.entities ?: return

        val playerResource = entities.getByDtName("CDOTA_PlayerResource") ?: return
        heroes.clear()
        for (p in 0..9) {
            val heroHandle = playerResource.getEntityProperty<Int>("m_vecPlayerTeamData.%i.m_hSelectedHero", p)
                ?: continue
            entities.getByHandle(heroHandle)?.let { heroes.add(it) }

        }
    }

    private fun onEntityUpdate(entityUpdate: EntityUpdate) {
        if (entityUpdate.entity.dtClass?.dtName == "CDOTA_PlayerResource") {
            markDirty()
        }
    }

    private fun markDirty() {
        isDirty = true
    }
}