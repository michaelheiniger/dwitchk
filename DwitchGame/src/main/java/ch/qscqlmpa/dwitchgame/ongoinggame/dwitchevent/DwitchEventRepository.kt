package ch.qscqlmpa.dwitchgame.ongoinggame.dwitchevent

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DwitchEventRepository {
    fun getCardExchangeEvent(): Single<CardExchange>
    fun observeCardExchangeEvents(): Observable<CardExchange>
}