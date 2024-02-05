package services.entityProvider

import services.Disposable
import skadistats.clarity.model.Entity

interface HeroEntitiesProvider : EntitiesProvider, Disposable {
    override fun dispose()

    override fun retrieveEntities(): List<Entity>
}