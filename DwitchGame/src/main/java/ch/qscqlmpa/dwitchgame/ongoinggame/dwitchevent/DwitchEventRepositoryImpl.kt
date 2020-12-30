package ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DwitchEventRepositoryImpl @Inject constructor(
    private val store: InGameStore
) : DwitchEventRepository {

    override fun getCardExchangeEvent(): Single<CardExchange> {
        return store.getCardExchangeEvent()
    }

    override fun observeCardExchangeEvents(): Observable<CardExchange> {
        return store.observeCardExchangeEvents()
    }
}