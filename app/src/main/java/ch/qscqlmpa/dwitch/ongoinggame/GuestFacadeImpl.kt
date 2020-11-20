package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import io.reactivex.Observable
import javax.inject.Inject

internal class GuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
) : GuestFacade {

    override fun connect() {
        guestCommunicator.connect()
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return guestCommunicator.observeCommunicationState()
    }
}