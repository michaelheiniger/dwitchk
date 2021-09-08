package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchgame.ingame.usecases.GameLaunchableEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface WaitingRoomHostFacade {

    /**
     * Add a computer-managed player to the game.
     */
    fun addComputerPlayer(): Completable

    /**
     * Kick the player off the game. Can either be a human or computer player.
     */
    fun kickPlayer(player: PlayerWrUi): Completable

    /**
     * Cancel the current game. This game won't be resumable because it won't have been launched (see [launchGame]).
     */
    fun cancelGame(): Completable

    /**
     * Tell whether the game can be launched. If so, it can be launched using [launchGame]
     */
    fun observeGameLaunchableEvents(): Observable<GameLaunchableEvent>

    /**
     * Launch the game if all the requirements are fulfilled. See [observeGameLaunchableEvents].
     * The Completable fails if the requirements are **not** fulfilled.
     */
    fun launchGame(): Completable
}
