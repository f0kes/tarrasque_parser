package model

import kotlinx.serialization.Serializable
import model.enums.Team

/*message Game
{
    repeated Hero hero = 1; //100 * 10 = 1000 bytes = 1kb
    repeated Npc npc = 2; //21 * 100 = 2100 bytes = 2kb
    repeated Building building = 3; // 6 * 100 = 600 bytes = 1kb
    optional int32 game_time = 4; //4 bytes

}*/
@Serializable
class GameModel {
    var gameTime: Int = 0
    var heroes: List<HeroModel> = listOf()
    var npcs: List<NpcModel> = listOf()
    var buildings: List<BuildingModel> = listOf()
}

/*message Hero
{
    optional int32 hero_id = 1; //4 bytes
    optional Team team = 2; //1 byte
    optional int32 level = 3; //4 bytes
    optional Position position = 4; //12 bytes
    optional float health = 5; //4 bytes
    optional float max_health = 6; //4 bytes
    optional float mana = 7; //4 bytes
    optional float max_mana = 8; //4 bytes
    repeated Ability abilities = 9;// 4 bytes * 6 = 24 bytes
    optional int32 seen_ago = 10; //4 bytes
    repeated Item items = 11; // 5 bytes * 6 = 30 bytes
    optional int32 networth = 12; //4 bytes
    optional bool alive = 13; //1 byte

    //total = 100 bytes
}*/
@Serializable
class HeroModel {
    var heroId: Int = 0
    var team: Team = Team.NEUTRAL
    var level: Int = 0
    var position: PositionModel = PositionModel(0f, 0f, 0f)
    var health: Float = 0f
    var maxHealth: Float = 0f
    var mana: Float = 0f
    var maxMana: Float = 0f
    var abilities: List<AbilityModel> = listOf()
    var seenAgo: Int = 0
    var items: List<ItemModel> = listOf()
    var networth: Int = 0
    var alive: Boolean = false
}


/*message Npc
{
    optional int32 npc_id = 1; //4 bytes
    optional Team team = 2; //1 byte
    optional Position position = 3; //12 bytes
    optional int32 seen_ago = 4; //4 bytes
    //total = 21 bytes
}*/

@Serializable
class NpcModel {
    var npcId: Int = 0
    var team: Team = Team.NEUTRAL
    var position: PositionModel = PositionModel(0f, 0f, 0f)
    var seenAgo: Int = 0
}


/*message Position
{
    optional float x = 1;
    optional float y = 2;
    optional float z = 3;

    //total = 12 bytes
}*/
@Serializable
data class PositionModel(val x: Float, val y: Float, val z: Float) {
    override fun toString(): String {
        return "PositionModel(x=$x, y=$y, z=$z)"
    }

    operator fun plus(other: PositionModel): PositionModel {
        return PositionModel(x + other.x, y + other.y, z + other.z)
    }
}

/*message Ability
{
    optional float cooldown = 1;
}*/

@Serializable
class AbilityModel {
    var cooldown: Float = 0f
}

/*message Item
{
    optional int32 item_id = 1;
    optional bool cooldown = 3;
}*/

@Serializable
class ItemModel {
    var itemId: Int = 0
    var cooldown: Boolean = false
}

/*message Building
{
    optional int32 building_id = 1;
    optional Team team = 2;
    optional bool alive = 3;

    //total = 6 bytes
}*/

@Serializable
class BuildingModel {
    var buildingId: Int = 0
    var team: Team = Team.NEUTRAL
    var alive: Boolean = false
}


/*enum Team
{
    RADIANT = 0;
    DIRE = 1;
    NEUTRAL = 2;
}*/



