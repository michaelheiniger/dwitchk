package ch.qscqlmpa.dwitchgame.ongoinggame.game.events


import ch.qscqlmpa.dwitchgame.ongoinggame.communication.EventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import javax.inject.Inject

@OngoingGameScope
internal class GuestGameEventRepository @Inject constructor() : EventRepository<GuestGameEvent>()