package ch.qscqlmpa.dwitchcommunication.gameadvertising

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.ConnectionState
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.GameInfo
import ch.qscqlmpa.dwitchcommunication.WLanConnectionRepository
import ch.qscqlmpa.dwitchcommunication.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchcommunication.common.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class GameAdvertiserImpl @Inject constructor(
    applicationConfigRepository: ApplicationConfigRepository,
    private val wLanConnectionRepository: WLanConnectionRepository,
    private val network: Network,
    private val serializerFactory: SerializerFactory,
    private val schedulerFactory: SchedulerFactory
) : GameAdvertiser {

    private val destinationPort = applicationConfigRepository.config.gameAdvertising.port

    companion object {
        private const val DELAY_BEFORE_FIRST_AD_SECONDS: Long = 0
        private const val ADVERTISING_PERIOD_SECONDS: Long = 2
    }

    override fun observeSerializedGameAdvertisingInfo(gameInfo: GameInfo): Observable<AdvertisingInfo> {
        return wLanConnectionRepository.observeConnectionState()
            .map { state ->
                when (state) {
                    is ConnectionState.OnWifi -> AdvertisingInfo.Info(
                        serializerFactory.serialize(
                            GameAdvertisingInfo(
                                gameInfo,
                                state.ipAddress
                            )
                        )
                    )
                    ConnectionState.Other -> AdvertisingInfo.NoInfoAvailable
                }
            }
    }

    override fun advertiseGame(gameInfo: GameInfo): Completable {
        return wLanConnectionRepository.observeConnectionState()
            .switchMapCompletable { state ->
                when (state) {
                    is ConnectionState.OnWifi -> advertiseGame(gameInfo, state.ipAddress)
                    ConnectionState.Other -> Completable.complete()
                }
            }
    }

    private fun advertiseGame(gameInfo: GameInfo, ipAddress: String): Completable {
        val ad = GameAdvertisingInfo(gameInfo, ipAddress)
        Logger.info { "Advertise game: $ad" }
        return advertisingScheduler()
            .subscribeOn(schedulerFactory.timeScheduler())
            .doOnNext { network.sendAdvertisement(destinationPort, gameInfoAsStr(ad)) }
            .doFinally { Logger.info { "Game no longer advertised." } }
            .ignoreElements()
            .onErrorComplete()
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
