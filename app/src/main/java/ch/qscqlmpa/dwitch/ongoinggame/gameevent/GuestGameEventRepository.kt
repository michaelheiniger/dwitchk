package ch.qscqlmpa.dwitch.ongoinggame.gameevent

import ch.qscqlmpa.dwitch.ongoinggame.events.EventRepository
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class GuestGameEventRepository @Inject
constructor() : EventRepository<GuestGameEvent>()