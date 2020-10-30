package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

data class RejoinInfo(
    val gameCommonId: GameCommonId,
    val player: Player,
    val connectionID: LocalConnectionId
) {
    fun inGameId(): PlayerInGameId {
        return player.inGameId
    }
}