package services.stringTableProvider

import benchmarks.Benchmark
import runner.Runner
import services.entityMapper.EntityMapper
import services.entityMapper.IEntityMapper
import services.runnerRegistry.RunnerRegistry
import skadistats.clarity.ClarityException
import skadistats.clarity.event.Insert
import skadistats.clarity.model.Entity
import skadistats.clarity.model.StringTable
import skadistats.clarity.processor.stringtables.OnStringTableCreated
import skadistats.clarity.processor.stringtables.StringTables
import skadistats.clarity.processor.stringtables.UsesStringTable

@UsesStringTable("*")
class StringTableProvider(runnerRegistry: RunnerRegistry, private val entityMapper: IEntityMapper) :
    Runner(runnerRegistry) {
    @Insert
    private val stringTables: StringTables? = null
    fun getEntityName(entity: Entity): String? {
        if (stringTables == null) return null
        val nameIndex = entity.getProperty<Int>("m_pEntity.m_nameStringableIndex") ?: return null
        if (nameIndex == -1) return null
        return stringTables.forName("EntityNames").getNameByIndex(nameIndex)?.toString()
    }

    fun getEntityNameId(prefix: String, entity: Entity): Int? {
        if (stringTables == null) return null
        val name = getEntityName(entity) ?: return null
        return entityMapper.getId(prefix, name)
    }

    fun getEntityDTClassId(prefix: String, entity: Entity): Int? {
        if (stringTables == null) return null
        val dtName = entity.dtClass?.dtName ?: return null
        return entityMapper.getId("${prefix}_dt_", dtName)
    }

    fun dumpEntityNames() {
        val table = stringTables!!.forName("EntityNames")
        var i = 0
        while (table.hasIndex(i)) {
            println(table.getNameByIndex(i))
            i++
        }
    }
}