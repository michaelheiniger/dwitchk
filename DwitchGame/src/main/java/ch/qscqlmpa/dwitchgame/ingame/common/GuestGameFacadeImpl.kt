package ch.qscqlmpa.dwitchgame.ingame.common

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GuestGameFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
    private val communicationStateRepository: GuestCommunicationStateRepository
) : GuestGameFacade, GuestCommunicator {

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
