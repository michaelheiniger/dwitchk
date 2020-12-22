package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

sealed class HostCommunicationState {
    object Open: HostCommunicationState()
    object Closed: HostCommunicationState()
    object Error: HostCommunicationState()
}