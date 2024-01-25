package services.entityUpdateProvider

import events.Event
import events.EventListener
import runner.Runner
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.event.Insert
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath
import skadistats.clarity.processor.entities.Entities
import skadistats.clarity.processor.entities.OnEntityUpdated
import skadistats.clarity.processor.entities.UsesEntities


@UsesEntities
class EntityUpdateProvider(runnerRegistry: RunnerRegistry) : Runner(runnerRegistry) {
    @Insert
    val entities: Entities? = null
    private val entityUpdateEvent = Event<EntityUpdate>()

    fun subscribe(listener: EventListener<EntityUpdate>) {
        entityUpdateEvent.subscribe(listener)
    }

    fun unsubscribe(listener: EventListener<EntityUpdate>) {
        entityUpdateEvent.unsubscribe(listener)
    }

    @OnEntityUpdated
    protected fun onEntityUpdated(e: Entity, changedFieldPaths: Array<FieldPath>, nChangedFieldPaths: Int) {
        entityUpdateEvent.invoke(EntityUpdate(e, changedFieldPaths, nChangedFieldPaths))
    }
}
