package ch.qscqlmpa.dwitch.ongoinggame.events

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.Resource

sealed class GuestCommunicationState(val resourceId: Resource) {
    object Connected: GuestCommunicationState(Resource(R.string.connected_to_host))
    object Disconnected: GuestCommunicationState(Resource(R.string.disconnected_from_host))
    object Error: GuestCommunicationState(Resource(R.string.guest_connection_error))
}