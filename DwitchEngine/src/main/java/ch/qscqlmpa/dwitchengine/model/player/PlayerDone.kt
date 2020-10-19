package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDone(val playerId: PlayerInGameId, val cardPlayedIsJoker: Boolean)