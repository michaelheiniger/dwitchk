package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

//TODO: Create mappers for resources in UI module
sealed class HostCommunicationState {
    object Open: HostCommunicationState()
    object Closed: HostCommunicationState()
    object Error: HostCommunicationState()
}