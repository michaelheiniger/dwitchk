package ch.qscqlmpa.dwitchgame.ingame.gameadvertising

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.GameInfo
import ch.qscqlmpa.dwitchcommunication.gameadvertising.AdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.gameadvertising.GameAdvertiser
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.tinylog.kotlin.Logger
import javax.inject.Inject

// TODO:Rename class and interface ?
internal class GameAdvertisingFacadeImpl @Inject constructor(
    private val store: InGameStore,
    private val gameAdvertiser: GameAdvertiser,
    private val schedulerFactory: SchedulerFactory
) : GameAdvertisingFacade {

    override fun observeAdvertisingInfo(): Observable<AdvertisingInfo> {
        return fetchGameInfo()
            .subscribeOn(schedulerFactory.io())
            .flatMapObservable(gameAdvertiser::observeSerializedGameAdvertisingInfo)
    }

    override fun advertiseGame(): Completable {
        return fetchGameInfo()
            .subscribeOn(schedulerFactory.io())
            .flatMapCompletable(gameAdvertiser::advertiseGame)
            .doFinally { Logger.warn { "Will no longer advertise game for this game" } }
    }

    private fun fetchGameInfo() = Single.fromCallable {
        val game = store.getGame()
        GameInfo(
            game.isNew(),
            game.name,
            game.gameCommonId,
        )
    }
}
