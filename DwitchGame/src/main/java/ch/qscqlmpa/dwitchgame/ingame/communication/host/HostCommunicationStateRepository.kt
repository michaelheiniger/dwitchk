package ch.qscqlmpa.dwitchgame.ingame.communication.host

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@InGameScope
internal class HostCommunicationStateRepository @Inject constructor(
    private val deviceConnectivityRepository: DeviceConnectivityRepository
) : CommunicationStateRepository {

    // Cache last event
    private val relay =
        BehaviorRelay.createDefault<ServerEvent.CommunicationEvent.ServerState>(ServerEvent.CommunicationEvent.StoppedListeningForConnections)

    override fun connectedToGame(): Observable<Boolean> = currentState().map { state ->
        when (state) {
            HostCommunicationState.Online -> true
            HostCommunicationState.Starting,
            is HostCommunicationState.OfflineFailed,
            is HostCommunicationState.OfflineDisconnected -> false
        }
    }

    fun currentState(): Observable<HostCommunicationState> {
        return Observable.combineLatest(
            relay,
            deviceConnectivityRepository.observeConnectionState()
                .map { state -> state is DeviceConnectionState.ConnectedToWlan }
        ) { commEvent, connectedToWlan ->
            if (connectedToWlan) {
                when (commEvent) {
                    ServerEvent.CommunicationEvent.StartingServer -> HostCommunicationState.Starting
                    ServerEvent.CommunicationEvent.ListeningForConnections -> HostCommunicationState.Online
                    is ServerEvent.CommunicationEvent.ErrorListeningForConnections -> HostCommunicationState.OfflineFailed(
                        connectedToWlan = true
                    )
                    ServerEvent.CommunicationEvent.StoppedListeningForConnections -> HostCommunicationState.OfflineDisconnected(
                        connectedToWlan = true
                    )
                }
            } else {
                if (commEvent is ServerEvent.CommunicationEvent.ErrorListeningForConnections) {
                    HostCommunicationState.OfflineFailed(connectedToWlan = false)
                } else {
                    // The server might still be running, the host can't communication with guests when not on a WLAN
                    HostCommunicationState.OfflineDisconnected(connectedToWlan = false)
                }
            }
        }.distinctUntilChanged()
    }

    fun notifyEvent(event: ServerEvent.CommunicationEvent.ServerState) {
        Logger.info { "Notify new communication event: $event" }
        return relay.accept(event)
    }
}
