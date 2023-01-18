package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import javax.inject.Inject

/**
 * Occurs if the host closes the connection or if the connection is broken.
 * --> it does NOT occur if the guest closes the connection itself (e.g. by leaving the game).
 */
internal class GuestDisconnectedFromHostEventProcessor @Inject constructor(
    commStateRepository: GuestCommunicationStateRepository
) : BaseGuestCommunicationEventProcessor(commStateRepository)
