package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import ch.qscqlmpa.dwitchstore.model.Player

object EntityMapper {

    fun toPlayerWr(player: Player): PlayerWr {
        return PlayerWr(player.dwitchId, player.name, player.playerRole, player.connectionState, player.ready)
    }

    fun toPlayerWrUi(player: Player): PlayerWrUi {
        return PlayerWrUi(player.name, player.connectionState, player.ready)
    }
}
