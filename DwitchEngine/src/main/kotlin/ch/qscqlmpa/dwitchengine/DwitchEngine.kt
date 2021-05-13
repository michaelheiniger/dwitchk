package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import org.tinylog.kotlin.Logger

/**
 * Engine enforcing the rules for the game Dwitch.
 * The actions are performed for the current player, see [DwitchGameState.currentPlayerId].
 */
interface DwitchEngine {

    /**
     * Returns the information about the game intended for the UI.
     */
    fun getGameInfo(): DwitchGameInfo

    /**
     * Current player plays [cardsPlayed].
     * The value of the card(s) must be equal or greater than the previous card(s) played or be a joker.
     * Several cards can be played if they have the same [Card.name] and if the number of cards played matches
     * the number of cards that have been played before (if the table is not empty).
     * @return the updated game state
     */
    fun playCards(cardsPlayed: PlayedCards): DwitchGameState

    /**
     * Current player passes its turn. It won't be allowed to play a card until the table is cleared (i.e. a joker is played or
     * at most one player can play remains in the active players)
     * @return the updated game state
     */
    fun passTurn(): DwitchGameState

    /**
     * Starts a new round.
     * @return the updated game state
     */
    fun startNewRound(cardDealerFactory: CardDealerFactory): DwitchGameState

    /**
     * @return the card exchange info for the player with id [playerId] if this player is required to perform a card exchange.
     * If not, null is returned.
     */
    fun getCardExchangeIfRequired(playerId: DwitchPlayerId): DwitchCardExchange?

    /**
     * Set cards chosen for exchange by player with id [playerId].
     * @return the updated game state
     * @throws IllegalArgumentException in two cases:
     * 1) the player with id [playerId] is not supposed to perform a card exchange
     * 2) the cards chosen for the exchange by player with id [playerId] are invalid (e.g. cards not in the hand of the player,
     * constraint on card minimum value broken).
     */
    fun chooseCardsForExchange(playerId: DwitchPlayerId, cards: Set<Card>): DwitchGameState

    fun isLastCardPlayedTheFirstJackOfTheRound(): Boolean

    fun joker(): CardName

    companion object {

        /**
         * Create a new game.
         * @return the full state of the game. Only intended to be used as argument of [DwitchEngine]
         * and is not supposed to be read/written by clients of [DwitchEngine].
         * See [DwitchEngine.getGameInfo] for game info intended for UI.
         */
        fun createNewGame(players: List<DwitchPlayerOnboardingInfo>, initialGameSetup: InitialGameSetup): DwitchGameState {
            Logger.debug { "Start new game, players:  $players, initial game setup: $initialGameSetup" }
            return GameBootstrap.createNewGame(players, initialGameSetup)
        }
    }
}
