package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.EndGameUsecase
import io.reactivex.Completable
import javax.inject.Inject

internal class GameRoomHostFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val endGameUsecase: EndGameUsecase
) : GameRoomHostFacade{

    override fun consumeLastEvent(): GuestGameEvent? {
        return gameEventRepository.consumeLastEvent()
    }

    override fun endGame(): Completable {
        return endGameUsecase.endGame()
    }
}