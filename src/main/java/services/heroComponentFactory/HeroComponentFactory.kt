package services.heroComponentFactory

import components.HeroComponent
import services.entityProvider.HeroEntitiesProvider
import skadistats.clarity.model.Entity

class HeroComponentFactory(private val entitiesProvider: HeroEntitiesProvider) {
    //private var heroComponents: MutableList<HeroComponent> = mutableListOf()
    private val entityToComponentMap: MutableMap<Entity, HeroComponent> = mutableMapOf()
    private var lastEntitiesSet: MutableSet<Entity> = mutableSetOf()


    fun retrieveHeroComponents(): List<HeroComponent> {
        val newEntitiesSet: MutableSet<Entity> = mutableSetOf()
        val entities = entitiesProvider.retrieveEntities()
        for (entity in entities) {
            newEntitiesSet.add(entity)
            if (entityToComponentMap.contains(entity)) continue
            val heroComponent = HeroComponent(entity)
            entityToComponentMap[entity] = heroComponent
        }
        for (entity in lastEntitiesSet) {
            if (newEntitiesSet.contains(entity)) continue
            val heroComponent = entityToComponentMap[entity] ?: continue
            heroComponent.dispose()
            entityToComponentMap.remove(entity)
        }
        lastEntitiesSet = newEntitiesSet
        return entityToComponentMap.values.toList()
    }
}