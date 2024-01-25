package lookups

import skadistats.clarity.model.*
import skadistats.clarity.processor.entities.Entities

class HeroLookup(private val heroEntity: Entity, list: Entities) {
    private val fpCellX: FieldPath
    private val fpCellY: FieldPath
    private val fpCellZ: FieldPath
    private val fpVecX: FieldPath
    private val fpVecY: FieldPath
    private val fpVecZ: FieldPath
    private val fpHealth: FieldPath
    private val fpMaxHealth: FieldPath
    private val fpMana: FieldPath
    private val fpMaxMana: FieldPath
    private val fpLevel: FieldPath

    //todo: add modifiers
    private val fpItems = arrayOfNulls<FieldPath>(6)
    private val list: Entities

    init {
        val heroClass = heroEntity.dtClass
        this.fpCellX = getBodyComponentFieldPath(heroClass, "cellX")
        this.fpCellY = getBodyComponentFieldPath(heroClass, "cellY")
        this.fpCellZ = getBodyComponentFieldPath(heroClass, "cellZ")
        this.fpVecX = getBodyComponentFieldPath(heroClass, "vecX")
        this.fpVecY = getBodyComponentFieldPath(heroClass, "vecY")
        this.fpVecZ = getBodyComponentFieldPath(heroClass, "vecZ")
        this.fpHealth = heroClass.getFieldPathForName("m_iHealth")
        this.fpMaxHealth = heroClass.getFieldPathForName("m_iMaxHealth")
        this.fpMana = heroClass.getFieldPathForName("m_flMana")
        this.fpMaxMana = heroClass.getFieldPathForName("m_flMaxMana")
        this.fpLevel = heroClass.getFieldPathForName("m_iCurrentLevel")
        for (i in 0..5) {
            fpItems[i] = heroClass.getFieldPathForName(String.format("m_hItems.%04d", i))
        }
        this.list = list
    }

    fun getBodyComponentFieldPath(heroClass: DTClass, which: String?): FieldPath {
        return heroClass.getFieldPathForName(String.format("CBodyComponent.m_%s", which))
    }

    fun isPositionChanged(e: Entity, changedFieldPaths: Array<FieldPath>, nChangedFieldPaths: Int): Boolean {
        if (e !== heroEntity) return false
        for (f in 0 until nChangedFieldPaths) {
            val changedFieldPath = changedFieldPaths[f]
            if (changedFieldPath == fpCellX) return true
            if (changedFieldPath == fpCellY) return true
            if (changedFieldPath == fpCellZ) return true
            if (changedFieldPath == fpVecX) return true
            if (changedFieldPath == fpVecY) return true
            if (changedFieldPath == fpVecZ) return true
        }
        return false
    }

    fun areItemsChanged(e: Entity, changedFieldPaths: Array<FieldPath>, nChangedFieldPaths: Int): Boolean {
        if (e !== heroEntity) return false
        for (f in 0 until nChangedFieldPaths) {
            val changedFieldPath = changedFieldPaths[f]
            for (i in 0..5) {
                if (changedFieldPath == fpItems[i]) return true
            }
        }
        return false
    }

    val position: Vector
        get() = Vector(
                getPositionComponent(fpCellX, fpVecX),
                getPositionComponent(fpCellY, fpVecY),
                getPositionComponent(fpCellZ, fpVecZ)
        )

    fun GetItems(entityNames: StringTable): Array<String?> {
        val items = arrayOfNulls<String>(6)
        for (i in 0..5) {
            val handle = heroEntity.getPropertyForFieldPath<Int>(fpItems[i])
            val item = list.getByHandle(handle)
            if (item == null) {
                items[i] = "null"
            } else {
                val stringTableIndexFP = item.dtClass.getFieldPathForName("m_pEntity.m_nameStringableIndex")
                if (stringTableIndexFP != null) {
                    val stIdx = item.getPropertyForFieldPath<Int>(stringTableIndexFP)
                    val entityName = entityNames.getNameByIndex(stIdx)
                    items[i] = "($entityName)"
                }
            }
        }
        return items
    }

    fun getPositionComponent(fpCell: FieldPath?, fpVec: FieldPath?): Float {
        val cell = heroEntity.getPropertyForFieldPath<Int>(fpCell)
        val vec = heroEntity.getPropertyForFieldPath<Float>(fpVec)
        return cell * 128.0f + vec
    }
}
