package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface WaitingRoomFacade {

    fun observePlayers(): Observable<List<PlayerWrUi>>

    fun isGameANewGame(): Single<Boolean>
}

data class PlayerWrUi(val name: String, val connectionState: PlayerConnectionState, val ready: Boolean)
