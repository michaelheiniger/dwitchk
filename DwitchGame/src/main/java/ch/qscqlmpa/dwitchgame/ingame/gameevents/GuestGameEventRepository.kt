package ch.qscqlmpa.dwitchgame.ingame.gameevents

import ch.qscqlmpa.dwitchgame.common.CachedEventRepository
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class GuestGameEventRepository @Inject constructor() : CachedEventRepository<GuestGameEvent>()
