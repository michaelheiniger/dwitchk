package ch.qscqlmpa.dwitch.ongoinggame.events

import ch.qscqlmpa.dwitch.service.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class GuestCommunicationEventRepository @Inject
constructor() : EventRepository<GuestCommunicationState>()