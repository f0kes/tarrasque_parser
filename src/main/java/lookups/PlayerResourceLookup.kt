package lookups

import skadistats.clarity.io.Util
import skadistats.clarity.model.DTClass
import skadistats.clarity.model.Entity
import skadistats.clarity.model.FieldPath

class PlayerResourceLookup(playerResourceClass: DTClass, idx: Int) {
    private val fpSelectedHero: FieldPath = playerResourceClass.getFieldPathForName(
            String.format("m_vecPlayerTeamData.%s.m_hSelectedHero", Util.arrayIdxToString(idx))
    )

    fun isSelectedHeroChanged(playerResource: Entity?, changedFieldPaths: Array<FieldPath>, nChangedFieldPaths: Int): Boolean {
        for (f in 0 until nChangedFieldPaths) {
            val changedFieldPath = changedFieldPaths[f]
            if (changedFieldPath == fpSelectedHero) return true
        }
        return false
    }

    fun getSelectedHeroHandle(playerResource: Entity): Int {
        return playerResource.getPropertyForFieldPath(fpSelectedHero)
    }
}
