package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface GameDashboardFacade {
    fun playCard(cardPlayed: Card): Completable
    fun pickCard(): Completable
    fun passTurn(): Completable
    fun startNewRound(): Completable
    fun observeDashboardInfo(): Observable<GameDashboardInfo>
    fun getCardExchangeInfo(): Single<CardExchangeInfo>
    fun observeCardExchangeEvents(): Observable<CardExchange>
    fun submitCardsForExchange(cards: Set<Card>): Completable
}