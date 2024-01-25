package components


import model.HeroModel
import model.Component
import services.Services.get
import services.entityPropertyGetter.getEntityProperty
import services.entityPropertyGetter.getPropertyIfChanged
import services.entityUpdateProvider.EntityUpdate
import services.entityUpdateProvider.EntityUpdateProvider
import services.stringTableProvider.StringTableProvider
import services.ticker.ITicker
import skadistats.clarity.model.Entity


class HeroComponent(private val heroEntity: Entity) : Component<HeroModel> {
    private val heroModel: HeroModel = HeroModel()
    private val secondEventListener = { time: Int -> this.onSecond(time) }
    private val updateEventListener = { upd: EntityUpdate -> this.onEntityUpdate(upd) }

    private val stringTableProvider: StringTableProvider = get<StringTableProvider>()
    private val ticker = get<ITicker>()
    private val entityUpdateProvider = get<EntityUpdateProvider>()

    init {
        ticker.subscribeToSecond(secondEventListener)
        entityUpdateProvider.subscribe(updateEventListener)
    }

    fun dispose() {
        ticker.unsubscribeFromSecond(secondEventListener)
        entityUpdateProvider.unsubscribe(updateEventListener)
    }

    private fun onSecond(time: Int) {

        heroModel.maxHealth = heroEntity.getEntityProperty<Int>("m_iMaxHealth")?.toFloat()
            ?: heroModel.maxHealth
        heroModel.level = heroEntity.getEntityProperty<Int>("m_iCurrentLevel")
            ?: heroModel.level

        println("hero: ${stringTableProvider.getEntityName(heroEntity)}, max health: ${heroModel.maxHealth}, time: $time, lvl: ${heroModel.level}")
    }

    private fun onEntityUpdate(entityUpdate: EntityUpdate) {
        heroModel.maxHealth = heroEntity.getPropertyIfChanged<Int>(entityUpdate, "m_iMaxHealth")?.toFloat()
            ?: heroModel.maxHealth

        heroModel.level = heroEntity.getPropertyIfChanged<Int>(entityUpdate, "m_iCurrentLevel")
            ?: heroModel.level
    }


    override fun retrieveModel(): HeroModel {
        return heroModel
    }


}
