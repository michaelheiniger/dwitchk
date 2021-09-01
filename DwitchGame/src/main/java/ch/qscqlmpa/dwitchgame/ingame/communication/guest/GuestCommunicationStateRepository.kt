package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@OngoingGameScope
internal class GuestCommunicationStateRepository @Inject constructor() : CommunicationStateRepository {

    override fun connected(): Observable<Boolean> =
        relay.map { state ->
            when (state) {
                GuestCommunicationState.Connected -> true
                GuestCommunicationState.Connecting,
                GuestCommunicationState.Error,
                GuestCommunicationState.Disconnected -> false
            }
        }

    // Cache last event
    private val relay = BehaviorRelay.createDefault<GuestCommunicationState>(GuestCommunicationState.Disconnected)

    fun currentState(): Observable<GuestCommunicationState> {
        return relay.distinctUntilChanged()
    }

    fun updateState(state: GuestCommunicationState) {
        Logger.info { "New communication state: $state" }
        return relay.accept(state)
    }
}
