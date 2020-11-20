package ch.qscqlmpa.dwitch.home

import ch.qscqlmpa.dwitch.home.usecases.NewGameUsecase
import io.reactivex.Completable
import javax.inject.Inject

internal class HomeHostFacadeImpl @Inject constructor(
    private val newGameUsecase: NewGameUsecase
) : HomeHostFacade {

    override fun hostGame(gameName: String, playerName: String): Completable {
        return newGameUsecase.hostGame(gameName, playerName)
    }
}