package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface GameDashboardFacade {
    fun playCard(cardPlayed: Card): Completable
    fun pickCard(): Completable
    fun passTurn(): Completable
    fun startNewRound(): Completable
    fun observeGameInfoForDashboard(): Observable<GameInfoForDashboard>
    fun getDashboard(): Single<GameInfoForDashboard>
    fun observeConnectionState(): Observable<PlayerConnectionState>
    fun getCardExchangeEvent(): Single<CardExchange>
    fun observeCardExchangeEvents(): Observable<CardExchange>
    fun submitCardsForExchange(cards: Set<Card>): Completable
}