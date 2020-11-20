package ch.qscqlmpa.dwitch.gameadvertising

import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class GameAdvertising @Inject constructor(
    private val serializerFactory: SerializerFactory,
    private val schedulerFactory: SchedulerFactory,
    private val network: Network
) {

    companion object {
        private const val DESTINATION_PORT = 8888
        private const val DELAY_BEFORE_FIRST_AD_SECONDS: Long = 0
        private const val ADVERTISING_PERIOD_SECONDS: Long = 2
    }

    fun startAdvertising(gameInfo: GameInfo): Completable {
        Timber.i("Advertise game: $gameInfo")
        return advertisingScheduler()
            .subscribeOn(schedulerFactory.timeScheduler())
            .doOnNext { network.sendAdvertisement(DESTINATION_PORT, gameInfoAsStr(gameInfo)) }
            .doFinally { Timber.i("Game no longer advertised.") }
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

    private fun gameInfoAsStr(gameInfo: GameInfo): String {
        return serializerFactory.serialize(gameInfo)
    }
}