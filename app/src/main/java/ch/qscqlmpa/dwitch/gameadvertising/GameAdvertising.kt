package ch.qscqlmpa.dwitch.gameadvertising

import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class GameAdvertising @Inject constructor(
    private val schedulerFactory: SchedulerFactory,
    private val network: Network
) {

    fun startAdvertising(gameName: String): Completable {
        Timber.i("Advertise game: %s", gameName)
        return advertisingScheduler()
            .subscribeOn(schedulerFactory.timeScheduler())
            .doOnNext { network.sendAdvertisement(DESTINATION_PORT, gameName) }
            .doFinally { Timber.i("Game no longer advertised.") }
            .ignoreElements()
    }

    private fun advertisingScheduler(): Observable<Long> {
        return Observable.interval(
            INITIAL_DELAY_SECONDS,
            PERIOD_SECONDS,
            TimeUnit.SECONDS,
            schedulerFactory.timeScheduler()
        )
    }

    companion object {
        private const val DESTINATION_PORT = 8888
        private const val INITIAL_DELAY_SECONDS: Long = 0
        private const val PERIOD_SECONDS: Long = 2
    }
}