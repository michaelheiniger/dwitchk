package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.home.usecases.NewGameUsecase
import ch.qscqlmpa.dwitchgame.home.usecases.ResumeGameUsecase
import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeHostFacadeImpl @Inject constructor(
    private val store: Store,
    private val newGameUsecase: NewGameUsecase,
    private val resumeGameUsecase: ResumeGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : HomeHostFacade {

    override fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable {
        return newGameUsecase.hostGame(gameName, playerName, gamePort)
            .subscribeOn(schedulerFactory.io())
    }

    override fun resumeGame(gameId: Long, gamePort: Int): Completable {
        return resumeGameUsecase.hostResumedGame(gameId, gamePort)
            .subscribeOn(schedulerFactory.io())
    }

    override fun resumableGames(): Observable<List<ResumableGameInfo>> {
        return store.getResumableGamesInfo()
            .subscribeOn(schedulerFactory.io())
    }
}
