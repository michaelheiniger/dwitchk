package ch.qscqlmpa.dwitch.ongoinggame.events

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
class HostCommunicationEventRepository @Inject
constructor() : EventRepository<HostCommunicationState>()