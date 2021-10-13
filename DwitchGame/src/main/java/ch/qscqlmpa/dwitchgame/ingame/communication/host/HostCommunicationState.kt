package ch.qscqlmpa.dwitchgame.ingame.communication.host

sealed class HostCommunicationState {

    /**
     * The server is being setup to be able to receive connection requests from clients.
     */
    object Starting : HostCommunicationState()

    /**
     * The server is listening for connection requests from clients.
     */
    object Online : HostCommunicationState()

    sealed class Offline(open val connectedToWlan: Boolean) : HostCommunicationState()

    /**
     * The server is not listening for connection requests from clients.
     */
    data class OfflineDisconnected(override val connectedToWlan: Boolean) : Offline(connectedToWlan)

    /**
     * An error occurred and the server is not in a regular operating state.
     */
    data class OfflineFailed(override val connectedToWlan: Boolean) : Offline(connectedToWlan)
}
