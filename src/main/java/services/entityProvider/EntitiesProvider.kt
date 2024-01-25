package services.entityProvider

import skadistats.clarity.model.Entity

interface EntitiesProvider {
    fun retrieveEntities(): List<Entity>
}