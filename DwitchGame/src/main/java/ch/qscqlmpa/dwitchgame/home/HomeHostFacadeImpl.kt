package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.home.usecases.NewGameUsecase
import io.reactivex.Completable
import javax.inject.Inject

internal class HomeHostFacadeImpl @Inject constructor(
    private val newGameUsecase: NewGameUsecase
) : HomeHostFacade {

    override fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable {
        return newGameUsecase.hostGame(gameName, playerName, gamePort)
    }
}