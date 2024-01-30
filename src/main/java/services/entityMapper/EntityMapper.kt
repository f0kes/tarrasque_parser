package services.entityMapper

import benchmarks.Benchmark
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

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
        var enumId = jedis.hget(typePrefix + STRING_TO_INT_KEY, enumString)?.toInt()
        if (enumId == null) {
            enumId = jedis.incr(typePrefix + NEXT_ID_KEY).toInt()
            jedis.hset(typePrefix + STRING_TO_INT_KEY, enumString, enumId.toString())
            jedis.hset(typePrefix + INT_TO_STRING_KEY, enumId.toString(), enumString)
        }
        return enumId
    }

    override fun getString(typePrefix: String, enumId: Int): String? {

        return jedis.hget(typePrefix + INT_TO_STRING_KEY, enumId.toString())
    }

    fun getPrefixes(): List<String> {
        return jedis.smembers(PREFIXES).toList()
    }


    private fun initializePrefix(prefix: String) {

        if (jedis.sismember(PREFIXES, prefix)) {
            return
        }
        jedis.sadd(PREFIXES, prefix)
        jedis.set(prefix + NEXT_ID_KEY, "-1")
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

    private fun cacheAllTables(): Map<String, Map<String, String>> {
        val prefixes = getPrefixes()
        val cache = mutableMapOf<String, Map<String, String>>()
        for (prefix in prefixes) {
            cache[prefix + STRING_TO_INT_KEY] = jedis.hgetAll(prefix + STRING_TO_INT_KEY)
            cache[prefix + INT_TO_STRING_KEY] = jedis.hgetAll(prefix + INT_TO_STRING_KEY)
            cache[prefix + NEXT_ID_KEY] = mapOf(Pair(prefix + NEXT_ID_KEY, jedis.get(prefix + NEXT_ID_KEY)))
        }
        return cache
    }

}
