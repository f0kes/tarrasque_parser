package model.enums

import skadistats.clarity.model.Entity

enum class Team {
    RADIANT,
    DIRE,
    NEUTRAL;

    companion object {
        fun fromInt(value: Int): Team {
            return when (value) {
                2 -> RADIANT
                3 -> DIRE
                else -> NEUTRAL
            }
        }
    }

    fun toInt(): Int {
        return when (this) {
            RADIANT -> 0
            DIRE -> 1
            NEUTRAL -> 2
        }
    }
}
