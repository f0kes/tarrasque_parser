package services.entityProvider

import services.entityUpdateProvider.EntityUpdateProvider
import skadistats.clarity.model.Entity

//slower but more reliable
class HeroEntitiesProviderByDTClass(entityUpdateProvider: EntityUpdateProvider) : HeroEntitiesProvider {
    private val predicateEntityProvider: PredicateEntityProvider =
        PredicateEntityProvider(entityUpdateProvider) { e -> e.dtClass.dtName.contains("hero", ignoreCase = true) }

    override fun dispose() {

    }

    override fun retrieveEntities(): List<Entity> {
        return predicateEntityProvider.retrieveEntities()
    }
}