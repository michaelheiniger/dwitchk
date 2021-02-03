package ch.qscqlmpa.dwitchcommunication.websocket.client

sealed class ClientCommunicationEvent {
    object ConnectedToHost : ClientCommunicationEvent()
    object DisconnectedFromHost : ClientCommunicationEvent()
    data class ConnectionError(val error: String?) : ClientCommunicationEvent()
}
