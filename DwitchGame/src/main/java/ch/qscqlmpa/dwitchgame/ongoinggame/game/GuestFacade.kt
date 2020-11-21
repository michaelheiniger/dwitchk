package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import io.reactivex.Observable

interface GuestFacade {
    fun connect()
    fun closeConnection()
    fun observeCommunicationState(): Observable<GuestCommunicationState>
}