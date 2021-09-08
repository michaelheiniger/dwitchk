package ch.qscqlmpa.dwitchgame.ingame

import ch.qscqlmpa.dwitchgame.ingame.usecases.EndGameUsecase
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class InInGameHostFacadeImpl @Inject constructor(
    private val endGameUsecase: EndGameUsecase
) : InGameHostFacade {

    override fun endGame(): Completable {
        return endGameUsecase.endGame()
    }
}
