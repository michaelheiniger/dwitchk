package ch.qscqlmpa.dwitchgame.ingame.gameevents

import ch.qscqlmpa.dwitchgame.common.CachedEventRepository
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import javax.inject.Inject

@InGameScope
internal class GuestGameEventRepository @Inject constructor() : CachedEventRepository<GuestGameEvent>()
