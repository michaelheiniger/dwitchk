package ch.qscqlmpa.dwitchgame.appevent

import ch.qscqlmpa.dwitchgame.di.GameScope
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@GameScope
class AppEventRepository @Inject constructor() {

    private val eventRelay = PublishRelay.create<AppEvent>()

    fun observeEvents(): Observable<AppEvent> {
        return eventRelay
    }

    fun notify(event: AppEvent) {
        eventRelay.accept(event)
    }
}