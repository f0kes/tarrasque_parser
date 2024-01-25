package services.heroComponentFactory

import components.HeroComponent
import services.entityProvider.HeroEntitiesProvider

class HeroComponentFactory(private val entitiesProvider: HeroEntitiesProvider) {
    private var heroComponents: MutableList<HeroComponent> = mutableListOf()

    //todo: only create new components if there are new entities
    fun retrieveHeroComponents(): List<HeroComponent> {
        for (heroComponent in heroComponents) {
            heroComponent.dispose()
        }
        heroComponents.clear()
        val entities = entitiesProvider.retrieveEntities()
        for (entity in entities) {
            heroComponents.add(HeroComponent(entity))
        }
        return heroComponents
    }
}