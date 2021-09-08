package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import io.reactivex.rxjava3.core.Observable

interface GuestCommunicationFacade {

    /**
     * Connects to the server.
     */
    fun connect()

    /**
     * Disconnects from the server.
     */
    fun disconnect()

    /**
     * Emits the current communication state of the guest.
     */
    fun currentCommunicationState(): Observable<GuestCommunicationState>
}
