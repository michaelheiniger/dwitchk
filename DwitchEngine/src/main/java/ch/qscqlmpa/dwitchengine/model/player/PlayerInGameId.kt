package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInGameId(val value: Long) : Comparable<PlayerInGameId> {

    override fun compareTo(other: PlayerInGameId): Int {
        return value.compareTo(other.value)
    }
}