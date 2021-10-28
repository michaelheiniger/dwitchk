package ch.qscqlmpa.dwitchgame.game

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.game.usecases.HostNewGameUsecase
import ch.qscqlmpa.dwitchgame.game.usecases.JoinNewGameUsecase
import ch.qscqlmpa.dwitchgame.game.usecases.JoinResumedGameUsecase
import ch.qscqlmpa.dwitchgame.game.usecases.ResumeGameUsecase
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class GameFacadeImpl @Inject constructor(
    private val store: Store,
    private val hostNewGameUsecase: HostNewGameUsecase,
    private val resumeGameUsecase: ResumeGameUsecase,
    private val joinNewGameUsecase: JoinNewGameUsecase,
    private val joinResumedGameUsecase: JoinResumedGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : GameFacade {

    override fun hostGame(gameName: String, playerName: String): Completable {
        return hostNewGameUsecase.hostGame(gameName, playerName)
            .subscribeOn(schedulerFactory.io())
    }

    override fun resumableGames(): Observable<List<ResumableGameInfo>> {
        return store.getResumableGamesInfo()
            .subscribeOn(schedulerFactory.io())
    }

    override fun resumeGame(gameId: Long): Completable {
        return resumeGameUsecase.hostResumedGame(gameId)
            .subscribeOn(schedulerFactory.io())
    }

    override fun joinGame(advertisedGame: GameAdvertisingInfo, playerName: String): Completable {
        return joinNewGameUsecase.joinGame(advertisedGame, playerName)
            .subscribeOn(schedulerFactory.io())
    }

    override fun joinResumedGame(advertisedGame: GameAdvertisingInfo): Single<RoomType> {
        return joinResumedGameUsecase.joinResumedGame(advertisedGame)
            .subscribeOn(schedulerFactory.io())
    }
}
