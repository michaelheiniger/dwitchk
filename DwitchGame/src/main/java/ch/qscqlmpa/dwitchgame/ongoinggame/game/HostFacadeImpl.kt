package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import io.reactivex.Completable
import javax.inject.Inject

internal class HostFacadeImpl @Inject constructor(
    private val hostCommunicator: HostCommunicator,
    private val gameAdvertising: GameAdvertising
) : HostFacade {

    override fun listenForConnections() {
        hostCommunicator.listenForConnections()
    }

    override fun closeAllConnections() {
        hostCommunicator.closeAllConnections()
    }

    override fun advertiseGame(gameAdvertisingInfo: ch.qscqlmpa.dwitchmodel.gamediscovery.GameAdvertisingInfo): Completable {
        return this.gameAdvertising.start(gameAdvertisingInfo)
    }
}