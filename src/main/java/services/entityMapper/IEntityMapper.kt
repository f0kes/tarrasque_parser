package services.entityMapper

interface IEntityMapper {
    fun getId(typePrefix: String, enumString: String): Int
    fun getString(typePrefix: String, enumId: Int): String?
    fun exportAllTables(): String
}