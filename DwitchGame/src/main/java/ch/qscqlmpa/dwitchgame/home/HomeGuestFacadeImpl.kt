package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitchgame.home.usecases.JoinNewGameUsecase
import ch.qscqlmpa.dwitchgame.home.usecases.JoinResumedGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeGuestFacadeImpl @Inject constructor(
    private val advertisedGameRepository: AdvertisedGameRepository,
    private val joinNewGameUsecase: JoinNewGameUsecase,
    private val joinResumedGameUsecase: JoinResumedGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : HomeGuestFacade {

    override fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return advertisedGameRepository.listenForAdvertisedGames()
    }

    override fun getAdvertisedGame(ipAddress: String): AdvertisedGame? {
        return advertisedGameRepository.getGame(ipAddress)
    }

    override fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return joinNewGameUsecase.joinGame(advertisedGame, playerName)
            .subscribeOn(schedulerFactory.io())
    }

    override fun joinResumedGame(advertisedGame: AdvertisedGame): Completable {
        return joinResumedGameUsecase.joinResumedGame(advertisedGame)
            .subscribeOn(schedulerFactory.io())
    }
}
