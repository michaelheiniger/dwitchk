package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@OngoingGameScope
internal class GuestCommunicationStateRepository @Inject constructor() {

    // Cache last event
    private val relay = BehaviorRelay.create<GuestCommunicationState>()

    fun currentState(): Observable<GuestCommunicationState> {
        return relay
    }

    fun updateState(state: GuestCommunicationState) {
        Logger.info { "New communication state: $state" }
        return relay.accept(state)
    }
}
