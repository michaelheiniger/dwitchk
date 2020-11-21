package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface WaitingRoomHostFacade {

    fun launchGame(): Completable

    fun cancelGame(): Completable

    fun gameCanBeLaunched(): Observable<GameLaunchableEvent>

    fun observeCommunicationState(): Observable<HostCommunicationState>
}