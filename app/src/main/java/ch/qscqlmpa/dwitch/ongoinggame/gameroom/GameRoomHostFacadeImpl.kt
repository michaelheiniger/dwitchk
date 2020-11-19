package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.ongoinggame.usecases.EndGameUsecase
import io.reactivex.Completable
import javax.inject.Inject

internal class GameRoomHostFacadeImpl @Inject constructor(
    private val endGameUsecase: EndGameUsecase
) : GameRoomHostFacade{

    override fun endGame(): Completable {
        return endGameUsecase.endGame()
    }
}