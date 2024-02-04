package components

import kotlinx.serialization.json.Json
import model.*
import model.enums.Team

import services.entityPropertyGetter.getPosition
import services.entityUpdateProvider.EntityUpdateProvider
import services.heroComponentFactory.HeroComponentFactory
import services.inputStreamProcessor.InputStreamProcessor
import services.stringTableProvider.StringTableProvider
import services.ticker.ITicker
import skadistats.clarity.model.Entity
import java.io.File

class GameComponent(
    private val heroComponentFactory: HeroComponentFactory,
    private val stringTableProvider: StringTableProvider,
    private val inputStreamProcessor: InputStreamProcessor,
    private val ticker: ITicker,
    private val entityUpdateProvider: EntityUpdateProvider,
    private val fullModel: FullModel
) : Component<GameModel> {
    val model: GameModel = GameModel()
    private val secondEventListener = { time: Int -> this.onSecond(time) }

    init {
        ticker.subscribeToSecond(secondEventListener)
    }

    fun dispose() {
        ticker.unsubscribeFromSecond(secondEventListener)
    }


    private fun onSecond(time: Int) {
        model.gameTime = time
        model.heroes = heroComponentFactory.retrieveHeroComponents().map { it.heroModel }
        model.npcs = retrieveNpcs()
        if (time % 60 == 0) {
            //writeModelJSONToFile(time.toString())
        }
        //writeModelJSONToFile(time.toString())
        fullModel.snapshots.add(model)
    }


    private fun createNpcHeroModel(entity: Entity): NpcModel? {
        val npcModel = NpcModel()
        npcModel.npcId = stringTableProvider.getEntityNameId("npc", entity) ?: npcModel.npcId
        npcModel.team = Team.fromInt(entity.getProperty<Int>("m_iTeamNum") ?: -1)
        if (npcModel.team == Team.NEUTRAL) return null
        npcModel.position = entity.getPosition()
        //retreive health if building

        //todo: last seen ago, last seen position
        return npcModel
    }


    private fun isEntityNpc(entity: Entity): Boolean {
        val dtName = entity.dtClass?.dtName ?: return false
        return dtName.contains("npc", ignoreCase = true)
    }


    private fun retrieveNpcs(): List<NpcModel> {
        val entities = entityUpdateProvider.entities ?: return emptyList()
        val npcs = mutableListOf<NpcModel>()
        for (entity in entities.getAllByPredicate { e -> isEntityNpc(e) }) {
            val npcModel = createNpcHeroModel(entity) ?: continue
            npcs.add(npcModel)
        }
        return npcs
    }


    override fun retrieveModel(): GameModel {
        return model
    }

    //todo: remove

    private fun writeModelJSONToFile(prefix: String, path: String = "src/main/resources/") {

        val model = retrieveModel()
        val jsonModel = Json.encodeToString(GameModel.serializer(), model)
        val jsonFile = File("${path}${prefix}_gameModel.json")
        //if file does not exist, then create it
        if (!jsonFile.exists()) {
            jsonFile.createNewFile()
        }
        jsonFile.writeText(jsonModel)
    }
}