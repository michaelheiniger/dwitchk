package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameRoomGuestFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository
) : GameRoomGuestFacade {

    override fun observeEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
    }

    override fun consumeLastEvent(): GuestGameEvent? {
        return gameEventRepository.consumeLastEvent()
    }
}