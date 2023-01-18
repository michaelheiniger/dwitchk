package ch.qscqlmpa.dwitchcommunication.gameadvertising

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.GameInfo
import ch.qscqlmpa.dwitchcommunication.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchcommunication.common.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class GameAdvertiserImpl @Inject constructor(
    applicationConfigRepository: ApplicationConfigRepository,
    private val deviceConnectivityRepository: DeviceConnectivityRepository,
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
        return deviceConnectivityRepository.observeConnectionState()
            .map { state ->
                when (state) {
                    is DeviceConnectionState.ConnectedToWlan -> AdvertisingInfo.Info(
                        serializerFactory.serialize(
                            GameAdvertisingInfo(
                                gameInfo,
                                state.ipAddress
                            )
                        )
                    )
                    DeviceConnectionState.NotConnectedToWlan -> AdvertisingInfo.NoInfoAvailable
                }
            }
    }

    override fun advertiseGame(gameInfo: GameInfo): Completable {
        return deviceConnectivityRepository.observeConnectionState()
            .switchMapCompletable { state ->
                when (state) {
                    is DeviceConnectionState.ConnectedToWlan -> advertiseGame(gameInfo, state.ipAddress)
                    DeviceConnectionState.NotConnectedToWlan -> Completable.complete()
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
