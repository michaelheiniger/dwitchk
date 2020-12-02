package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.EndGameUsecase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameRoomHostFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val endGameUsecase: EndGameUsecase
) : GameRoomHostFacade {

    override fun consumeLastEvent(): GuestGameEvent? {
        return gameEventRepository.consumeLastEvent()
    }

    override fun endGame(): Completable {
        return endGameUsecase.endGame()
    }
}