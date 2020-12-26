package ch.qscqlmpa.dwitchgame.ongoinggame

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DwitchEventRepository @Inject constructor(
    private val store: InGameStore
){

    fun getCardExchangeEvent(): Single<CardExchange> {
        return store.getCardExchangeEvent()
    }

    fun observeCardExchangeEvents(): Observable<CardExchange> {
        return store.observeCardExchangeEvents()
    }
}