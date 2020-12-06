package ch.qscqlmpa.dwitchgame.ongoinggame

import ch.qscqlmpa.dwitchmodel.game.DwitchEventBase
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

class DwitchEventRepository @Inject constructor(
    private val store: InGameStore
){

    fun observeCardExchangeEvents(): Observable<DwitchEventBase.CardExchange> {
        return store.observeDwitchEvents()
            .filter { event -> event is DwitchEventBase.CardExchange }
            .map { event -> event as DwitchEventBase.CardExchange }
    }

    fun consumeEvent(event: DwitchEventBase): Completable {
        return Completable.fromAction {
            val numDeletedEvents = store.deleteDwitchEvent(event)
            val expectedNumDeletedEvents = 1
            if (numDeletedEvents != expectedNumDeletedEvents) {
                Timber.e("Expected number of deleted event: $expectedNumDeletedEvents, actual: $numDeletedEvents")
                //TODO: How could this be handled ?
            }
        }
    }
}