package ch.qscqlmpa.dwitchgame.ongoinggame.common

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
    private val communicationStateRepository: GuestCommunicationStateRepository
) : GuestFacade, GuestCommunicator {

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
