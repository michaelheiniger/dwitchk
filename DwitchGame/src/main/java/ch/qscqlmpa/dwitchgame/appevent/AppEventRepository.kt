package ch.qscqlmpa.dwitchgame.appevent

import ch.qscqlmpa.dwitchgame.di.GameScope
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

@GameScope
class AppEventRepository @Inject constructor() {

    private val eventRelay = PublishRelay.create<AppEvent>()

    fun observeEvents(): Observable<AppEvent> {
        Timber.d("observing app events...")
        return eventRelay
    }

    fun notify(event: AppEvent) {
        Timber.d("Notify of event: $event")
        eventRelay.accept(event)
    }
}
