package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import javax.inject.Inject

internal class GuestStoppedEventProcessor @Inject constructor(
    commStateRepository: GuestCommunicationStateRepository,
) : BaseGuestCommunicationEventProcessor(commStateRepository)
