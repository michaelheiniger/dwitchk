package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface WaitingRoomFacade {

    fun observePlayers(): Observable<List<PlayerWrUi>>

    fun gameInfo(): Single<GameInfoUi>
}

data class PlayerWrUi(
    val id: Long,
    val name: String,
    val connected: Boolean,
    val ready: Boolean,
    val kickable: Boolean = false
)

data class GameInfoUi(val name: String, val gameIsNew: Boolean)
