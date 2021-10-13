package ch.qscqlmpa.dwitchgame.ingame.communication.host

import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@InGameScope
internal class HostCommunicationStateRepository @Inject constructor() : CommunicationStateRepository {

    override fun connectedToHost(): Observable<Boolean> = relay.map { state ->
        when (state) {
            HostCommunicationState.Open -> true
            HostCommunicationState.Opening,
            HostCommunicationState.Error,
            HostCommunicationState.Closed -> false
        }
    }

    // Cache last event
    private val relay = BehaviorRelay.createDefault<HostCommunicationState>(HostCommunicationState.Closed)

    fun currentState(): Observable<HostCommunicationState> {
        return relay
    }

    fun updateState(state: HostCommunicationState) {
        Logger.info { "New communication state: $state" }
        return relay.accept(state)
    }
}
