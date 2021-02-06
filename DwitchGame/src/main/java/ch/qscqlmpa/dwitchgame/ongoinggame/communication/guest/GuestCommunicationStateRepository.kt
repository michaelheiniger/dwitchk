package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
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
