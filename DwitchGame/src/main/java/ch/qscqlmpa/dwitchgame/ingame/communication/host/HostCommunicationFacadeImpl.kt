package ch.qscqlmpa.dwitchgame.ingame.communication.host

import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HostCommunicationFacadeImpl @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val communicator: HostCommunicator,
) : HostCommunicationFacade {

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
