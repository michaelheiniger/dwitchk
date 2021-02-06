package ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DwitchEventRepository {
    fun getCardExchangeInfo(): Single<CardExchangeInfo>
    fun observeCardExchangeEvents(): Observable<CardExchange>
}
