package ch.qscqlmpa.dwitchgame.gameadvertising

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameAdvertisingFacadeImpl @Inject constructor(
    private val gameAdvertising: GameAdvertising,
    private val schedulerFactory: SchedulerFactory
) : GameAdvertisingFacade {

    override fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable {
        return gameAdvertising.advertiseGame(gameAdvertisingInfo)
            .subscribeOn(schedulerFactory.io())
    }
}