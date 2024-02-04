package services.entityProvider

import services.Disposable
import services.entityUpdateProvider.EntityUpdate
import services.entityUpdateProvider.EntityUpdateProvider
import skadistats.clarity.model.Entity

//todo: remove code duplication
class PredicateEntityProvider(private val entityUpdateProvider:EntityUpdateProvider) : EntitiesProvider, Disposable {
    private val npcs: MutableSet<Entity> = mutableSetOf()
    private val entityUpdateEventListener = { upd: EntityUpdate -> onEntityUpdate(upd) }

    init {
        entityUpdateProvider.subscribe(entityUpdateEventListener)
    }

    override fun dispose() {
        entityUpdateProvider.unsubscribe(entityUpdateEventListener)
    }

    private fun onEntityUpdate(entityUpdate: EntityUpdate) {

    }

    override fun retrieveEntities(): List<Entity> {
        return listOf()
    }

}
