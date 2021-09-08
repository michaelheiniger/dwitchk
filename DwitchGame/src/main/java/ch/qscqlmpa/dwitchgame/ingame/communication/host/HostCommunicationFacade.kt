package ch.qscqlmpa.dwitchgame.ingame.communication.host

import io.reactivex.rxjava3.core.Observable

interface HostCommunicationFacade {

    /**
     * Starts the communication server. Clients will be able to join. Game advertising starts.
     */
    fun startServer()

    /**
     * Stops the communication server. Existing connections are severed. Game advertising stops.
     */
    fun stopServer()

    /**
     * Emits the current communication state of the host.
     */
    fun currentCommunicationState(): Observable<HostCommunicationState>
}
