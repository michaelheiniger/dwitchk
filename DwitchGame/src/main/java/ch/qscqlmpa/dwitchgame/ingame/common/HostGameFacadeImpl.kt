package ch.qscqlmpa.dwitchgame.ingame.common

import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HostGameFacadeImpl @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val communicator: HostCommunicator,
    private val gameAdvertising: GameAdvertising
) : HostGameFacade, GameAdvertising by gameAdvertising {

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
