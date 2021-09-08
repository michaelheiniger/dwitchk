package ch.qscqlmpa.dwitchgame.ingame.communication.host

sealed class HostCommunicationState {

    /**
     * The server is being setup to be able to receive connection requests from clients.
     */
    object Opening : HostCommunicationState()

    /**
     * The server is listening for connection requests from clients.
     */
    object Open : HostCommunicationState()

    /**
     * The server is not listening for connection requests from clients.
     */
    object Closed : HostCommunicationState()

    /**
     * An error occurred and the server is not in a regular operating state.
     */
    object Error : HostCommunicationState()
}
