package ch.qscqlmpa.dwitchgame.gameadvertising

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gameadvertising.network.Network
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class GameAdvertisingImpl @Inject constructor(
    private val serializerFactory: SerializerFactory,
    private val schedulerFactory: SchedulerFactory,
    private val network: Network
) : GameAdvertising {

    companion object : KLogging() {
        private const val DESTINATION_PORT = 8888
        private const val DELAY_BEFORE_FIRST_AD_SECONDS: Long = 0
        private const val ADVERTISING_PERIOD_SECONDS: Long = 2
    }

    override fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable {
        logger.info { "Advertise game: $gameAdvertisingInfo" }
        return advertisingScheduler()
            .subscribeOn(schedulerFactory.timeScheduler())
            .doOnNext { network.sendAdvertisement(DESTINATION_PORT, gameInfoAsStr(gameAdvertisingInfo)) }
            .doFinally { logger.info { "Game no longer advertised." } }
            .ignoreElements()
    }

    private fun advertisingScheduler(): Observable<Long> {
        return Observable.interval(
            DELAY_BEFORE_FIRST_AD_SECONDS,
            ADVERTISING_PERIOD_SECONDS,
            TimeUnit.SECONDS,
            schedulerFactory.timeScheduler()
        )
    }

    private fun gameInfoAsStr(gameAdvertisingInfo: GameAdvertisingInfo): String {
        return serializerFactory.serialize(gameAdvertisingInfo)
    }
}
