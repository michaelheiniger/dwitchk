package ch.qscqlmpa.dwitchgame.ongoinggame.common

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface HostFacade {
    fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable
    fun startServer()
    fun stopServer()
    fun currentCommunicationState(): Observable<HostCommunicationState>
}
