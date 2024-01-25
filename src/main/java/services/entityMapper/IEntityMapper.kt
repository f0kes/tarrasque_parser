package services.entityMapper

interface IEntityMapper {
    fun getId(enumString: String): Int
    fun getString(enumId: Int): String?
}