package ch.qscqlmpa.dwitch.ongoinggame.gameevent

import ch.qscqlmpa.dwitch.service.OngoingGameScope
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

@OngoingGameScope
class GameEventRepository @Inject constructor() {

    private var lastEvent = AtomicReference<GameEvent>()

    private val relay = PublishRelay.create<GameEvent>()

    fun notifyOfEvent(event: GameEvent) {
        lastEvent.set(event)
        if (relay.hasObservers()) {
            relay.accept(event)
            lastEvent.set(null)
            Timber.d("New Game event emitted: %s", event)
        } else {
            Timber.d("New Game event (not emitted): %s", event)
        }
    }

    fun getLastEvent(): GameEvent? {
        val event = lastEvent.get()
        Timber.d("Game event consumed: %s", event)
        return event
    }

    fun observeEvents(): Observable<GameEvent> {
        return relay
    }
}