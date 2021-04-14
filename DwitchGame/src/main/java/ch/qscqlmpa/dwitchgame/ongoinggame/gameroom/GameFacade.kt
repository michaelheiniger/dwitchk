package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface GameFacade {
    fun observeGameData(): Observable<DwitchState>
    fun playCard(cardPlayed: Card): Completable
    fun passTurn(): Completable
    fun startNewRound(): Completable
    fun submitCardsForExchange(cards: Set<Card>): Completable
}

sealed class DwitchState {

    data class RoundIsBeginning(val info: GameDashboardInfo) : DwitchState()

    data class RoundIsOngoing(val info: GameDashboardInfo) : DwitchState()

    /**
     * Local player chooses card(s) to exchange.
     */
    data class CardExchange(val info: CardExchangeInfo) : DwitchState()

    /**
     * Local player either must not choose cards to exchange or has already chosen.
     */
    object CardExchangeOnGoing : DwitchState()

    data class EndOfRound(val info: EndOfRoundInfo) : DwitchState()
}