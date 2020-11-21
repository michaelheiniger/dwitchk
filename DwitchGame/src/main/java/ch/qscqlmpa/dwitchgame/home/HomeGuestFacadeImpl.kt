package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.usecases.NewGameUsecase
import io.reactivex.Completable
import javax.inject.Inject

internal class HomeGuestFacadeImpl @Inject constructor(
    private val newGameUsecase: NewGameUsecase
) : HomeGuestFacade {

    override fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return newGameUsecase.joinGame(advertisedGame, playerName)
    }
}