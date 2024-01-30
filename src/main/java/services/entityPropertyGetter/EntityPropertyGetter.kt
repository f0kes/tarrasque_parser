package services.entityPropertyGetter

import model.PositionModel
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

fun Entity.getPosition(): PositionModel {
    val x = getPositionCoordinate("X")
    val y = getPositionCoordinate("Y")
    val z = getPositionCoordinate("Z")
    val position = PositionModel(x, y, z)
    return position
}

fun Entity.getCell(): PositionModel? {
    val x = this.getEntityProperty<Int>("CBodyComponent.m_cellX")
    val y = this.getEntityProperty<Int>("CBodyComponent.m_cellY")
    val z = this.getEntityProperty<Int>("CBodyComponent.m_cellZ")
    if (x == null || y == null || z == null) return null
    return PositionModel(x.toFloat(), y.toFloat(), z.toFloat())
}

fun Entity.getPositionCoordinate(c: String): Float {
    val cell = this.getEntityProperty<Int>("CBodyComponent.m_cell${c}")
    val vec = this.getEntityProperty<Float>("CBodyComponent.m_vec${c}")
    if (cell == null || vec == null) return 0f
    return cell * 128.0f + vec;
}

fun String.formatProperty(idx: Int? = null): String {
    var p = this
    if (idx != null) {
        p = p.replace("%i", Util.arrayIdxToString(idx))
    }
    return p
}