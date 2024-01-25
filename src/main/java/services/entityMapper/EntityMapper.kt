package services.entityMapper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import redis.clients.jedis.Jedis

class EntityMapper(redisHost: String, redisPort: Int) : IEntityMapper {
    private val jedis = Jedis(redisHost, redisPort)

    companion object {
        const val STRING_TO_INT_KEY = "enumStringToInt"
        const val INT_TO_STRING_KEY = "enumIntToString"
        const val NEXT_ID_KEY = "nextEnumId"
    }

    init {
        // Initialize next ID if not present
        if (jedis.get(NEXT_ID_KEY) == null) {
            jedis.set(NEXT_ID_KEY, "1")
        }
    }

    override fun getId(enumString: String): Int {
        // Check if the string is already mapped
        var enumId = jedis.hget(STRING_TO_INT_KEY, enumString)?.toInt()
        if (enumId == null) {
            // Assign new ID and save
            enumId = jedis.incr(NEXT_ID_KEY).toInt()
            jedis.hset(STRING_TO_INT_KEY, enumString, enumId.toString())
            jedis.hset(INT_TO_STRING_KEY, enumId.toString(), enumString)
        }
        return enumId
    }

    override fun getString(enumId: Int): String? {
        return jedis.hget(INT_TO_STRING_KEY, enumId.toString())
    }

    fun exportTables(): String {
        val stringToIntMap = jedis.hgetAll(STRING_TO_INT_KEY)
        val intToStringMap = jedis.hgetAll(INT_TO_STRING_KEY)

        val exportData = mapOf(
            STRING_TO_INT_KEY to stringToIntMap,
            INT_TO_STRING_KEY to intToStringMap
        )

        return Json.encodeToString(exportData)
    }
}
