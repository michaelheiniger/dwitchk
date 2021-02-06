package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitchgame.home.usecases.NewGameUsecase
import ch.qscqlmpa.dwitchgame.home.usecases.ResumeGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeGuestFacadeImpl @Inject constructor(
    private val advertisedGameRepository: AdvertisedGameRepository,
    private val newGameUsecase: NewGameUsecase,
    private val resumeGameUsecase: ResumeGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : HomeGuestFacade {

    override fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return advertisedGameRepository.listenForAdvertisedGames()
            .subscribeOn(schedulerFactory.io())
    }

    override fun stopListeningForAdvertiseGames() {
        advertisedGameRepository.stopListening()
    }

    override fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return newGameUsecase.joinGame(advertisedGame, playerName)
            .subscribeOn(schedulerFactory.io())
    }

    override fun joinResumedGame(advertisedGame: AdvertisedGame): Completable {
        return resumeGameUsecase.joinResumedGame(advertisedGame)
            .subscribeOn(schedulerFactory.io())
    }
}
