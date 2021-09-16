package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommunication.model.Message
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GuestCommunicationFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
    private val communicationStateRepository: GuestCommunicationStateRepository
) : GuestCommunicationFacade, GuestCommunicator {

    override fun connect() {
        guestCommunicator.connect()
    }

    override fun disconnect() {
        guestCommunicator.disconnect()
    }

    override fun sendMessageToHost(message: Message) {
        guestCommunicator.sendMessageToHost(message)
    }

    override fun currentCommunicationState(): Observable<GuestCommunicationState> {
        return communicationStateRepository.currentState()
    }
}