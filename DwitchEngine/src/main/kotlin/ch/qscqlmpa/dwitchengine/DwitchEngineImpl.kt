package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.cardexchange.*
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurn
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurnGameUpdater
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurnState
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCard
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCardGameUpdater
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCardState
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRound
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRoundGameUpdater
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRoundState
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboardFactory
import ch.qscqlmpa.dwitchengine.rules.CardExchangeComputer
import org.tinylog.kotlin.Logger

/* Important rule: As soon as the last player to have played a card has to play again
(i.e. a table round has happened but no one could play a card, regardless of the reason: dwitched, pass, ...),
the table stack is removed and the player can play any card.
 */

/**
 * The Engine is executed by the current player. Hence the assumption is that the current player is the local player.
 */
internal class DwitchEngineImpl(private val currentGameState: DwitchGameState) : DwitchEngine {

    private val currentPlayerId = currentGameState.currentPlayerId

    override fun getGameInfo(): DwitchGameInfo {
        return PlayerDashboardFactory(currentGameState).create()
    }

    override fun playCard(cardPlayed: Card): DwitchGameState {
        Logger.debug { "Player $currentPlayerId plays card $cardPlayed, current game state: $currentGameState" }
        return PlayCard(
            PlayCardState(currentGameState, cardPlayed),
            PlayCardGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    override fun passTurn(): DwitchGameState {
        Logger.debug { "Player $currentPlayerId passes its turn, current game state: $currentGameState" }
        return PassTurn(
            PassTurnState(currentGameState),
            PassTurnGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    override fun startNewRound(cardDealerFactory: CardDealerFactory): DwitchGameState {
        Logger.debug { "Player $currentPlayerId starts a new round, current game state: $currentGameState" }
        return StartNewRound(
            StartNewRoundState(currentGameState),
            StartNewRoundGameUpdater(currentGameState),
            cardDealerFactory
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    override fun chooseCardsForExchange(playerId: DwitchPlayerId, cards: Set<Card>): DwitchGameState {
        Logger.debug { "Choose exchange cards $cards for Player $currentPlayerId, current game state: $currentGameState" }
        val gameStateUpdated = CardExchangeChooser(
            CardExchangeChooserState(currentGameState, playerId, cards),
            CardExchangeChooserGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)

        val cardExchangePerformer = CardExchangePerformer(
            CardExchangePerformerState(gameStateUpdated),
            CardExchangePerformerGameUpdater(gameStateUpdated)
        )
        if (cardExchangePerformer.cardExchangeReadyToBePerformed()) {
            return cardExchangePerformer
                .getUpdatedGameState()
                .also(this::logUpdatedGameState)
        }
        return gameStateUpdated
    }

    override fun getCardExchangeIfRequired(playerId: DwitchPlayerId): DwitchCardExchange? {
        if (currentGameState.phase != DwitchGamePhase.CardExchange) {
            return null
        }
        val player = currentGameState.player(playerId)

        if (player.cardsForExchange.isEmpty()) {
            return CardExchangeComputer.getCardExchange(player.id, player.rank, player.cardsInHand.toSet())
        }
        return null // Cards already chosen
    }

    private fun logUpdatedGameState(gameState: DwitchGameState) {
        Logger.debug { "Updated game state: $gameState" }
    }
}