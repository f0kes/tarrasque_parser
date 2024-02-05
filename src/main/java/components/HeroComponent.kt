package components


import benchmarks.Benchmark
import kotlinx.serialization.json.Json
import model.*
import model.enums.Team
import services.entityPropertyGetter.getEntityProperty
import services.entityPropertyGetter.getPosition
import services.entityUpdateProvider.EntityUpdateProvider
import services.stringTableProvider.StringTableProvider
import services.ticker.ITicker
import skadistats.clarity.model.Entity
import java.io.File


class HeroComponent(
    val heroEntity: Entity,
    private val ticker: ITicker,
    private val stringTableProvider: StringTableProvider,
    private val entityUpdateProvider: EntityUpdateProvider
) : Component<HeroModel> {
    val heroModel: HeroModel = HeroModel()
    private val secondEventListener = { time: Int -> this.onSecond(time) }

    private var abilityEntities = getAbilityEntities()

    init {
        ticker.subscribeToSecond(secondEventListener)
    }

    fun dispose() {
        ticker.unsubscribeFromSecond(secondEventListener)
    }


    fun markSeen() {
        heroModel.seenAgo = 0
        heroModel.lastSeenPosition = heroModel.position
    }


    private fun onSecond(time: Int) {

        heroModel.maxHealth = heroEntity.getEntityProperty<Int>("m_iMaxHealth")?.toFloat() ?: heroModel.maxHealth
        heroModel.level = heroEntity.getEntityProperty<Int>("m_iCurrentLevel") ?: heroModel.level
        heroModel.heroId = stringTableProvider.getEntityNameId("hero", heroEntity) ?: heroModel.heroId
        heroModel.items = generateItemModels(getItems())
        heroModel.team = Team.fromInt(heroEntity.getEntityProperty<Int>("m_iTeamNum") ?: -1)
        heroModel.position = heroEntity.getPosition()
        heroModel.health = heroEntity.getEntityProperty<Int>("m_iHealth")?.toFloat() ?: 0f
        heroModel.maxMana = heroEntity.getEntityProperty<Float>("m_flMaxMana") ?: heroModel.maxMana
        heroModel.mana = heroEntity.getEntityProperty<Float>("m_flMana") ?: 0f
        heroModel.alive = heroModel.health > 0
        heroModel.seenAgo += 1
        heroModel.abilities = retrieveAbilityModels()

    }


    private fun getItems(): Array<Entity?> {
        val items = arrayOfNulls<Entity?>(6)
        val entities = entityUpdateProvider.entities ?: return items
        for (i in 0..5) {
            val itemHandle = heroEntity.getEntityProperty<Int>("m_hItems.%04d".format(i))
            if (itemHandle != null) {
                val itemEntity = entities.getByHandle(itemHandle)
                items[i] = itemEntity
            } else {
                items[i] = null
            }
        }
        return items
    }


    private fun generateItemModels(itemEntities: Array<Entity?>): List<ItemModel> {
        val itemModels = mutableListOf<ItemModel>()
        for (itemEntity in itemEntities) {
            if (itemEntity != null) {
                val itemModel = ItemModel()
                itemModel.itemId = stringTableProvider.getEntityNameId("item", itemEntity) ?: itemModel.itemId
                itemModel.cooldown =
                    itemEntity.getEntityProperty<Float>("m_fCooldown") ?: itemModel.cooldown //TODO: does not work
                itemModels.add(itemModel)
            }
        }
        return itemModels
    }

    //todo: this is very slow

    private fun retrieveAbilityModels(): List<AbilityModel> {
        val abilityModels = mutableListOf<AbilityModel>()

        if (abilityEntities == null) abilityEntities = getAbilityEntities()
        val toIterate = abilityEntities ?: return abilityModels

        for (abilityEntity in toIterate) {
            val abilityModel = AbilityModel()

            //main overhead here
            abilityModel.abilityId =
                stringTableProvider.getEntityNameId("ability", abilityEntity) ?: abilityModel.abilityId
            abilityModel.level = abilityEntity.getEntityProperty<Int>("m_iLevel") ?: abilityModel.level
            abilityModel.cooldown =
                abilityEntity.getEntityProperty<Float>("m_fCooldown") ?: abilityModel.cooldown
            abilityModels.add(abilityModel)

        }
        return abilityModels
    }

    private fun getAbilityEntities(): List<Entity>? {
        val abilityEntities = mutableListOf<Entity>()
        val entities = entityUpdateProvider.entities?.getAllByPredicate { e -> isMyAbility(e) }
        if (entities == null) return null
        for (entity in entities) {
            abilityEntities.add(entity)
        }
        return abilityEntities
    }


    private fun isMyAbility(entity: Entity): Boolean {
        val isAbility = entity.dtClass?.dtName?.contains("_Ability_", ignoreCase = false) ?: false
        if (!isAbility) return false
        val ownerHandle = entity.getProperty<Int>("m_hOwnerEntity")
        if (ownerHandle != null) {
            val ownerEntity = entityUpdateProvider.entities?.getByHandle(ownerHandle)
            if (ownerEntity != null) {
                return ownerEntity == heroEntity
            }
        }
        return false
    }


    override fun retrieveModel(): HeroModel {
        return heroModel
    }

    //TODO:Move
    private fun writeModelJSONToFile(prefix: String, path: String = "src/main/resources/") {

        val model = retrieveModel()
        val jsonModel = Json.encodeToString(HeroModel.serializer(), model)
        val jsonFile = File("${path}${prefix}_${model.heroId}_heroModel.json")
        //if file does not exist, then create it
        if (!jsonFile.exists()) {
            jsonFile.createNewFile()
        }
        jsonFile.writeText(jsonModel)
    }


}
