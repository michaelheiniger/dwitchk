package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import ch.qscqlmpa.dwitchstore.model.Player

object EntityMapper {

    fun toPlayerWr(player: Player): PlayerWr {
        return PlayerWr(player.dwitchId, player.name, player.playerRole, player.connectionState, player.ready)
    }
}
