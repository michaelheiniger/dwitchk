package ch.qscqlmpa.dwitchgame.ongoinggame.gameevents

import ch.qscqlmpa.dwitchgame.common.CachedEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class GuestGameEventRepository @Inject constructor() : CachedEventRepository<GuestGameEvent>()
