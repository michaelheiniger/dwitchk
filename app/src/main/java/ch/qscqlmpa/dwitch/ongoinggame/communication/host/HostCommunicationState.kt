package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.Resource

sealed class HostCommunicationState(val resource: Resource) {
    object Open: HostCommunicationState(Resource(R.string.listening_for_guests))
    object Closed: HostCommunicationState(Resource(R.string.not_listening_for_guests))
    object Error: HostCommunicationState(Resource(R.string.host_connection_error))
}