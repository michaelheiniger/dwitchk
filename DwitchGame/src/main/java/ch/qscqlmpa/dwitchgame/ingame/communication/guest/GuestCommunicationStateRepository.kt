package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@InGameScope
internal class GuestCommunicationStateRepository @Inject constructor(
    private val deviceConnectivityRepository: DeviceConnectivityRepository,
) : CommunicationStateRepository {

    // Cache last event
    private val relay =
        BehaviorRelay.createDefault<ClientEvent.CommunicationEvent>(ClientEvent.CommunicationEvent.DisconnectedFromServer)

    override fun connectedToGame(): Observable<Boolean> =
        currentState().map { state ->
            when (state) {
                GuestCommunicationState.Connected -> true
                GuestCommunicationState.Connecting,
                is GuestCommunicationState.Error,
                is GuestCommunicationState.Disconnected -> false
            }
        }

    fun currentState(): Observable<GuestCommunicationState> {
        return Observable.combineLatest(
            relay,
            deviceConnectivityRepository.observeConnectionState()
                .map { state -> state is DeviceConnectionState.ConnectedToWlan }
        ) { commEvent, connectedToWifi ->
            when (commEvent) {
                ClientEvent.CommunicationEvent.ConnectingToServer -> GuestCommunicationState.Connecting
                ClientEvent.CommunicationEvent.ConnectedToServer -> GuestCommunicationState.Connected
                is ClientEvent.CommunicationEvent.ConnectionError -> GuestCommunicationState.Error(connectedToWifi)
                ClientEvent.CommunicationEvent.Stopped,
                ClientEvent.CommunicationEvent.DisconnectedFromServer -> GuestCommunicationState.Disconnected(connectedToWifi)
            }
        }
            .distinctUntilChanged()
    }

    fun notifyEvent(event: ClientEvent.CommunicationEvent) {
        Logger.info { "Notify new communication event: $event" }
        return relay.accept(event)
    }
}
