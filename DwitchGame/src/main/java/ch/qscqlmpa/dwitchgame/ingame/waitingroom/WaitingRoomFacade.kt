package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface WaitingRoomFacade {

    /**
     * Emits the current list of players in the game.
     */
    fun observePlayers(): Observable<List<PlayerWrUi>>

    /**
     * Emits info about the game.
     */
    fun gameInfo(): Single<GameInfoUi>
}

data class PlayerWrUi(
    /**
     * Id of the player in the Store.
     */
    val id: Long,

    /**
     * Name of the player
     */
    val name: String,

    /**
     * Whether the player is connected to the host.
     */
    val connected: Boolean,

    /**
     * Ready state of the player
     */
    val ready: Boolean,

    /**
     * Whether the game may be kicked from the game. A player may not be kicked from a resumed game.
     */
    val kickable: Boolean = false
)

data class GameInfoUi(

    /**
     * Name of the game
     */
    val name: String,

    /**
     * Whether the game is a new one or is a resumed one.
     */
    val gameIsNew: Boolean
)
