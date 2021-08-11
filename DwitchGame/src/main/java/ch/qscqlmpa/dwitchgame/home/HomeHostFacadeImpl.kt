package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.home.usecases.HostNewGameUsecase
import ch.qscqlmpa.dwitchgame.home.usecases.ResumeGameUsecase
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeHostFacadeImpl @Inject constructor(
    private val store: Store,
    private val hostNewGameUsecase: HostNewGameUsecase,
    private val resumeGameUsecase: ResumeGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : HomeHostFacade {

    override fun hostGame(gameName: String, playerName: String): Completable {
        return hostNewGameUsecase.hostGame(gameName, playerName)
            .subscribeOn(schedulerFactory.io())
    }

    override fun resumeGame(gameId: Long): Completable {
        return resumeGameUsecase.hostResumedGame(gameId)
            .subscribeOn(schedulerFactory.io())
    }

    override fun resumableGames(): Observable<List<ResumableGameInfo>> {
        return store.getResumableGamesInfo()
            .subscribeOn(schedulerFactory.io())
    }
}
