package ch.qscqlmpa.dwitch.ongoinggame.events

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class HostCommunicationEventRepository @Inject
constructor() : EventRepository<HostCommunicationState>()