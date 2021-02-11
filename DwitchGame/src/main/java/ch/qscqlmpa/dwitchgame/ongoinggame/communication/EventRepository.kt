package ch.qscqlmpa.dwitchgame.ongoinggame.communication

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
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
        logger.debug { "Last event consumed: $event" }
        return event
    }

    fun notify(event: T) {
        if (relay.hasObservers()) {
            relay.accept(event)
            lastEvent.set(null)
            logger.info { "New event consumed: $event" }
        } else {
            logger.info { "New event stored (waiting for a consumer): $event" }
            lastEvent.set(event)
        }
    }

    companion object : KLogging()
}
