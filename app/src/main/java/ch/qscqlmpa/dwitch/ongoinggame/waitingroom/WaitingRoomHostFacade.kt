package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface WaitingRoomHostFacade {

    fun launchGame(): Completable

    fun cancelGame(): Completable

    fun gameCanBeLaunched(): Observable<GameLaunchableEvent>

    fun observeCommunicationState(): Observable<HostCommunicationState>
}