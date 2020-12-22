package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface PlayerDashboardFacade {
    fun playCard(cardPlayed: Card): Completable
    fun pickCard(): Completable
    fun passTurn(): Completable
    fun startNewRound(): Completable
    fun observeDashboard(): Observable<PlayerDashboard>
    fun getDashboard(): Single<PlayerDashboard>
    fun observeConnectionState(): Observable<PlayerConnectionState>
    fun observeCardExchangeEvents(): Observable<CardExchange>
    fun submitCardsForExchange(cards: Set<Card>): Completable
}