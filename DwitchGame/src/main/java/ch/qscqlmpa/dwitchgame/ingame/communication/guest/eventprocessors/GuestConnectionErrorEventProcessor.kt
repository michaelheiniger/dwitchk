package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import javax.inject.Inject

internal class GuestConnectionErrorEventProcessor @Inject constructor(
    commStateRepository: GuestCommunicationStateRepository,
) : BaseGuestCommunicationEventProcessor(commStateRepository)
