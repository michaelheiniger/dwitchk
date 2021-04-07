package ch.qscqlmpa.dwitchgame.ongoinggame.common

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import io.reactivex.rxjava3.core.Observable

interface GuestFacade {
    fun connect()
    fun disconnect()
    fun currentCommunicationState(): Observable<GuestCommunicationState>
}
