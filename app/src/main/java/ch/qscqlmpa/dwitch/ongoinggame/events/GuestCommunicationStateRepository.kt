package ch.qscqlmpa.dwitch.ongoinggame.events

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

@OngoingGameScope
internal class GuestCommunicationStateRepository @Inject constructor() {

    // Cache last event
    private val relay = BehaviorRelay.create<GuestCommunicationState>()

    fun observeEvents(): Observable<GuestCommunicationState> {
        return relay
    }

    fun notify(state: GuestCommunicationState) {
        Timber.i("New communication state: $state")
        return relay.accept(state)
    }
}