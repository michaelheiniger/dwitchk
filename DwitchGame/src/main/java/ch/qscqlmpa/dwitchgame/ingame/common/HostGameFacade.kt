package ch.qscqlmpa.dwitchgame.ingame.common

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface HostGameFacade {
    fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable
    fun startServer()
    fun stopServer()
    fun currentCommunicationState(): Observable<HostCommunicationState>
}
