package ch.qscqlmpa.dwitchgame.ingame.common

import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import io.reactivex.rxjava3.core.Observable

interface GuestGameFacade {
    fun connect()
    fun disconnect()
    fun currentCommunicationState(): Observable<GuestCommunicationState>
}
