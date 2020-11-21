package ch.qscqlmpa.dwitchgame.ongoinggame.communication

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

/**
 * Features required:
 * - notify of an event
 * - event can only be consumed once (one subscriber at a time): we want the notified event to be cached until a subscriber
 * consumes it and a subscriber cannot consume it twice. The rational is that the consumer has a lifecycle tied to the UI
 * so we don't want to lose an event. At the same time, we don't want to perform an operation resulting of an event more than once.
 */
internal abstract class EventRepository<T> {

    private var lastEvent = AtomicReference<T?>()

    private val relay = PublishRelay.create<T>()

    fun observeEvents(): Observable<T> {
        return relay
    }

    fun consumeLastEvent(): T? {
        val event = lastEvent.get()
        Timber.d("Last event consumed: $event")
        return event
    }

    fun notify(event: T) {
        if (relay.hasObservers()) {
            relay.accept(event)
            lastEvent.set(null)
            Timber.i("New event consumed: $event")
        } else {
            Timber.i("New event stored (waiting for a consumer): $event")
            lastEvent.set(event)
        }
    }
}