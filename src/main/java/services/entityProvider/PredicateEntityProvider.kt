package services.entityProvider

import services.Disposable
import services.entityUpdateProvider.EntityUpdate
import services.entityUpdateProvider.EntityUpdateProvider
import skadistats.clarity.model.Entity
import skadistats.clarity.util.Predicate

class PredicateEntityProvider(
    private val entityUpdateProvider: EntityUpdateProvider, private val predicate: Predicate<Entity>
) : EntitiesProvider, Disposable {
    override fun dispose() {

    }

    override fun retrieveEntities(): List<Entity> {
        val result = mutableListOf<Entity>()
        if (entityUpdateProvider.entities != null) {
            for (e in entityUpdateProvider.entities.getAllByPredicate(predicate)) {
                result.add(e)
            }
        }
        return result
    }


}


