package ch.qscqlmpa.dwitchgame.ongoinggame.gameevents

import ch.qscqlmpa.dwitchgame.ongoinggame.common.EventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class GuestGameEventRepository @Inject constructor() : EventRepository<GuestGameEvent>()
