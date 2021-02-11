package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import javax.inject.Inject

@OngoingGameScope
internal class HostCommunicationStateRepository @Inject constructor() {

    // Cache last event
    private val relay = BehaviorRelay.create<HostCommunicationState>()

    fun currentState(): Observable<HostCommunicationState> {
        return relay
    }

    fun updateState(state: HostCommunicationState) {
        logger.info { "New communication state: $state" }
        return relay.accept(state)
    }

    companion object : KLogging()
}
