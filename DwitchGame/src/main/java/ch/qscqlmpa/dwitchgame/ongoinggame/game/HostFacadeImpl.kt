package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HostFacadeImpl @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val communicator: HostCommunicator,
    private val gameAdvertising: GameAdvertising
) : HostFacade, GameAdvertising by gameAdvertising {

    override fun currentCommunicationState(): Observable<HostCommunicationState> {
        return communicationStateRepository.currentState()
    }

    override fun startServer() {
        communicator.startServer()
    }

    override fun stopServer() {
        communicator.stopServer()
    }
}
