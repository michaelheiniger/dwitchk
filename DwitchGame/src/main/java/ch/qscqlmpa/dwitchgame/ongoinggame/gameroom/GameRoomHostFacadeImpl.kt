package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.EndGameUsecase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameRoomHostFacadeImpl @Inject constructor(
    private val endGameUsecase: EndGameUsecase
) : GameRoomHostFacade {

    override fun endGame(): Completable {
        return endGameUsecase.endGame()
    }
}
