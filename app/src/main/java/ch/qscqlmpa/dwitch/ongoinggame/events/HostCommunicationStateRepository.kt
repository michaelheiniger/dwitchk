package ch.qscqlmpa.dwitch.ongoinggame.events

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

@OngoingGameScope
internal class HostCommunicationStateRepository @Inject constructor() {

    // Cache last event
    private val relay = BehaviorRelay.create<HostCommunicationState>()

    fun observeEvents(): Observable<HostCommunicationState> {
        return relay
    }

    fun notify(state: HostCommunicationState) {
        Timber.i("New communication state: $state")
        return relay.accept(state)
    }
}