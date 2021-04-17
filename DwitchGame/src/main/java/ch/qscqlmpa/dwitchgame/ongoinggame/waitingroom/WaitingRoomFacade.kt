package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Observable

interface WaitingRoomFacade {

    fun observePlayers(): Observable<List<PlayerWrUi>>
}

data class PlayerWrUi(val name: String, val connectionState: PlayerConnectionState, val ready: Boolean)
