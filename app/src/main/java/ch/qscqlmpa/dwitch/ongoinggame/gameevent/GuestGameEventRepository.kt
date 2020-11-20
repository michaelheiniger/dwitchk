package ch.qscqlmpa.dwitch.ongoinggame.gameevent

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import ch.qscqlmpa.dwitch.ongoinggame.events.EventRepository
import javax.inject.Inject

@OngoingGameScope
internal class GuestGameEventRepository @Inject constructor() : EventRepository<GuestGameEvent>()