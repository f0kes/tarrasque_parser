package services.entityPropertyGetter

import services.entityUpdateProvider.EntityUpdate
import skadistats.clarity.io.Util
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath

class EntityPropertyGetter {

}

fun <T> Entity.getEntityProperty(property: String, idx: Int? = null): T? {
    var p = property
    val e = this
    try {
        p = p.formatProperty(idx)
        val fp = e.dtClass.getFieldPathForName(p)
        return e.getPropertyForFieldPath(fp)
    } catch (ex: Exception) {
        return null
    }
}

fun Entity.isPropertyChanged(update: EntityUpdate, property: String, idx: Int? = null): Boolean {
    if (update.entity != this) return false
    val changedFieldPaths = update.changedFieldPaths
    val nChangedFieldPaths = update.nChangedFieldPaths
    val p = property.formatProperty(idx)
    for (f in 0 until nChangedFieldPaths) {
        val changedFieldPath = changedFieldPaths[f]
        if (changedFieldPath == this.dtClass.getFieldPathForName(p)) return true
    }
    return false
}

fun <T> Entity.getPropertyIfChanged(update: EntityUpdate, property: String, idx: Int? = null): T? {
    if (isPropertyChanged(update, property, idx)) {
        return getEntityProperty(property, idx)
    }
    return null
}

fun String.formatProperty(idx: Int? = null): String {
    var p = this
    if (idx != null) {
        p = p.replace("%i", Util.arrayIdxToString(idx))
    }
    return p
}