package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitchgame.home.usecases.NewGameUsecase
import ch.qscqlmpa.dwitchgame.home.usecases.ResumeGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeGuestFacadeImpl @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val advertisedGameRepository: AdvertisedGameRepository,
    private val newGameUsecase: NewGameUsecase,
    private val resumeGameUsecase: ResumeGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : HomeGuestFacade {

    override fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return advertisedGameRepository.listenForAdvertisedGames()
    }

    override fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Completable.merge(
            listOf(
                waitForServiceToStart(),
                newGameUsecase.joinGame(advertisedGame, playerName)
            )
        ).subscribeOn(schedulerFactory.io())
    }

    override fun joinResumedGame(advertisedGame: AdvertisedGame): Completable {
        return Completable.merge(
            listOf(
                waitForServiceToStart(),
                resumeGameUsecase.joinResumedGame(advertisedGame)
            )
        ).subscribeOn(schedulerFactory.io())
    }

    private fun waitForServiceToStart() = appEventRepository.observeEvents()
        .filter { event -> event is AppEvent.GameSetupDone }
        .firstOrError()
        .ignoreElement()
}
