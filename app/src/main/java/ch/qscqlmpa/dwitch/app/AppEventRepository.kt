package ch.qscqlmpa.dwitch.app

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

sealed class AppEvent {
    object ServiceStarted : AppEvent()
}

@AppScope
class AppEventRepository @Inject constructor() {

    private val eventRelay = PublishRelay.create<AppEvent>()

    fun observeEvents(): Observable<AppEvent> {
        Logger.debug { "Observing app events..." }
        return eventRelay
    }

    fun notify(event: AppEvent) {
        Logger.debug { "Notify of event: $event" }
        eventRelay.accept(event)
    }
}
