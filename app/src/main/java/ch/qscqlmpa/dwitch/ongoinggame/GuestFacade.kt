package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import io.reactivex.Observable

interface GuestFacade {
    fun connect()
    fun observeCommunicationState(): Observable<GuestCommunicationState>
}