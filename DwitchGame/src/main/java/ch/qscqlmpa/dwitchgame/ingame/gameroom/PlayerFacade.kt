package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface PlayerFacade {

    /**
     * Emits the current value of the game state.
     */
    fun observeGameData(): Observable<DwitchState>

    /**
     * Performs an action for the local player.
     */
    fun performAction(action: GameAction): Completable

    /**
     * Emits the name of the game.
     */
    fun getGameName(): Single<String>
}

sealed class GameAction {

    /**
     * The local player plays one or several cards.
     */
    data class PlayCard(val cardsPlayed: PlayedCards) : GameAction()

    /**
     * The local player passes its turn.
     */
    object PassTurn : GameAction()

    /**
     * The local player (that must be the host) starts a new round.
     */
    object StartNewRound : GameAction()

    /**
     * The local player submits one or several cards for the exchange phase of the game.
     */
    data class SubmitCardsForExchange(val cards: Set<Card>) : GameAction()
}

sealed class DwitchState {

    /**
     * The round has just begun.
     */
    data class RoundIsBeginning(val info: GameDashboardInfo) : DwitchState()

    /**
     * The round has already begun and is on-going.
     */
    data class RoundIsOngoing(val info: GameDashboardInfo) : DwitchState()

    /**
     * The game is in the card-exchange phase: the local player must choose one or several cards to exchange.
     */
    data class CardExchange(val info: CardExchangeInfo) : DwitchState()

    /**
     * The game is in the card-exchange phase: the local player either must not choose cards to exchange or has already chosen.
     */
    object CardExchangeOnGoing : DwitchState()

    /**
     * The current round is over.
     */
    data class EndOfRound(val info: EndOfRoundInfo) : DwitchState()
}
