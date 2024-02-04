package services.entityProvider

import services.Disposable
import services.entityPropertyGetter.getEntityProperty
import services.entityUpdateProvider.EntityUpdate
import services.entityUpdateProvider.EntityUpdateProvider
import skadistats.clarity.model.Entity


class HeroEntitiesProvider(private val entityUpdateProvider: EntityUpdateProvider) : EntitiesProvider, Disposable {

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
        heroes.clear()
        val entities = entityUpdateProvider.entities ?: return
        val playerResource = entities.getByDtName("CDOTA_PlayerResource") ?: return

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