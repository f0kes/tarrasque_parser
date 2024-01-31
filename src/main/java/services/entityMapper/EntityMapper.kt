package services.entityMapper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import redis.clients.jedis.Jedis

class EntityMapper(redisHost: String, redisPort: Int) : IEntityMapper {
    private val jedis = Jedis(redisHost, redisPort)
    private val cache = cacheAllTables()


    companion object {
        const val STRING_TO_INT_KEY = "enumStringToInt"
        const val INT_TO_STRING_KEY = "enumIntToString"
        const val NEXT_ID_KEY = "nextEnumId"
        const val PREFIXES = "prefixes"
    }


    override fun getId(typePrefix: String, enumString: String): Int {
        initializePrefix(typePrefix)
        var enumId = cache.prefixes[typePrefix]?.stringToInt?.get(enumString)
        if (enumId == null) {
            enumId = increment(typePrefix)
            addDefinition(typePrefix, enumString, enumId)
        }
        return enumId
    }

    private fun addDefinition(typePrefix: String, enumString: String, enumId: Int) {
        jedis.hset(typePrefix + STRING_TO_INT_KEY, enumString, enumId.toString())
        jedis.hset(typePrefix + INT_TO_STRING_KEY, enumId.toString(), enumString)
        cache.prefixes[typePrefix]?.stringToInt?.put(enumString, enumId)
        cache.prefixes[typePrefix]?.intToString?.put(enumId, enumString)
    }

    private fun increment(typePrefix: String): Int {
        val enumId = jedis.incr(typePrefix + NEXT_ID_KEY).toInt()
        cache.prefixes[typePrefix]?.nextId = enumId
        return enumId
    }

    override fun getString(typePrefix: String, enumId: Int): String? {

        return jedis.hget(typePrefix + INT_TO_STRING_KEY, enumId.toString())
    }

    private fun getRedisPrefixes(): List<String> {
        return jedis.smembers(PREFIXES).toList()
    }


    private fun initializePrefix(prefix: String) {

        if (cache.prefixes.contains(prefix)) {
            return
        }
        jedis.sadd(PREFIXES, prefix)
        jedis.set(prefix + NEXT_ID_KEY, "-1")
        cache.prefixes[prefix] = PrefixData(-1, mutableMapOf(), mutableMapOf())

    }

    fun exportTables(prefix: String): String {

        val exportData = mutableMapOf<String, Map<String, String>>()
        exportData[prefix + STRING_TO_INT_KEY] = jedis.hgetAll(prefix + STRING_TO_INT_KEY)
        exportData[prefix + INT_TO_STRING_KEY] = jedis.hgetAll(prefix + INT_TO_STRING_KEY)
        exportData[prefix + NEXT_ID_KEY] = mapOf(Pair(prefix + NEXT_ID_KEY, jedis.get(prefix + NEXT_ID_KEY)))

        return Json.encodeToString(exportData)
    }

    fun exportAllTables(): String {
        return Json.encodeToString(cacheAllTables())
    }

    private fun cacheAllTables(): ExportData {
        val prefixes = mutableMapOf<String, PrefixData>()
        val prefixList = getRedisPrefixes()
        for (prefix in prefixList) {
            val prefixData = PrefixData(
                nextId = jedis.get(prefix + NEXT_ID_KEY).toInt(),
                stringToInt = with(jedis.hgetAll(prefix + STRING_TO_INT_KEY)) {
                    this.mapValues { it.value.toInt() }.toMutableMap()
                },
                intToString = with(jedis.hgetAll(prefix + INT_TO_STRING_KEY)) {
                    this.mapKeys { it.key.toInt() }.toMutableMap()
                }
            )
            prefixes[prefix] = prefixData
        }
        return ExportData(prefixes)
    }

    private data class ExportData(
        var prefixes: MutableMap<String, PrefixData>
    )

    private data class PrefixData(
        var nextId: Int,
        var stringToInt: MutableMap<String, Int>,
        var intToString: MutableMap<Int, String>
    )

}
