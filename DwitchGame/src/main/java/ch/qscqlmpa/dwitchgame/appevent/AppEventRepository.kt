package ch.qscqlmpa.dwitchgame.appevent

import ch.qscqlmpa.dwitchgame.di.GameScope
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import javax.inject.Inject

@GameScope
class AppEventRepository @Inject constructor() {

    private val eventRelay = PublishRelay.create<AppEvent>()

    fun observeEvents(): Observable<AppEvent> {
        logger.debug { "observing app events..." }
        return eventRelay
    }

    fun notify(event: AppEvent) {
        logger.debug { "Notify of event: $event" }
        eventRelay.accept(event)
    }

    companion object : KLogging()
}
