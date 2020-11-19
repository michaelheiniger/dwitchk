package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

sealed class ClientCommunicationEvent {
    object ConnectedToHost : ClientCommunicationEvent()
    object DisconnectedFromHost : ClientCommunicationEvent()
    data class ConnectionError(val error: String) : ClientCommunicationEvent()
}
