package services.entityUpdateProvider

import events.EventArgs
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath

class EntityUpdate(@JvmField val entity: Entity, val changedFieldPaths: Array<FieldPath>, val nChangedFieldPaths: Int) : EventArgs()
