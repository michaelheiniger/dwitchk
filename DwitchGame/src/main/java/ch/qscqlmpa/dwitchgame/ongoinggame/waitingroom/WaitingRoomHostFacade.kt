package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface WaitingRoomHostFacade {

    /**
     * Add a computer managed player to the game.
     */
    fun addComputerPlayer(): Completable

    /**
     * Launch the game if all the requirements are fulfilled. See [observeGameLaunchableEvents]
     */
    fun launchGame(): Completable

    fun cancelGame(): Completable

    /**
     * Kick player off the game.
     */
    fun kickPlayer(player: PlayerWrUi): Completable

    /**
     * Tell whether the game can be launched. If so, it can be launched using [launchGame]
     */
    fun observeGameLaunchableEvents(): Observable<GameLaunchableEvent>

    /**
     * Tell what the current state of the host communication wise.
     */
    fun observeCommunicationState(): Observable<HostCommunicationState>
}
