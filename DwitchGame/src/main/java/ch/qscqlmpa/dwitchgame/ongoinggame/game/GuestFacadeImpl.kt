package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
) : GuestFacade {

    override fun connect() {
        guestCommunicator.connect()
    }

    override fun closeConnection() {
        guestCommunicator.closeConnection()
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return guestCommunicator.observeCommunicationState()
    }
}