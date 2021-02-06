package ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class DwitchEventRepositoryImpl @Inject constructor(private val store: InGameStore) : DwitchEventRepository {

    override fun getCardExchangeInfo(): Single<CardExchangeInfo> {
        return Single.fromCallable { store.getCardExchangeInfo() }
    }

    override fun observeCardExchangeEvents(): Observable<CardExchange> {
        return store.observeCardExchangeEvents()
    }
}
