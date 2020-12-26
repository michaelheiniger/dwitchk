package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.cardexchange.*
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurn
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurnGameUpdater
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurnState
import ch.qscqlmpa.dwitchengine.actions.pickcard.PickCard
import ch.qscqlmpa.dwitchengine.actions.pickcard.PickCardGameUpdater
import ch.qscqlmpa.dwitchengine.actions.pickcard.PickCardState
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCard
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCardGameUpdater
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCardState
import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRound
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRoundGameUpdater
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRoundState
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboardFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.rules.CardExchangeComputer

/* Important rule: As soon as the last player to have played a card has to play again
(i.e. a table round has happened but no one could play a card, regardless of the reason: dwitched, pass, ...),
the table stack is removed and the player can play any card.
 */

/**
 * The Engine is executed by the current player. Hence the assumption is that the current player is the local player.
 */
class DwitchEngine(private val currentGameState: GameState) {

    private val currentPlayerId = currentGameState.currentPlayerId

    fun getPlayerDashboard(playerId: PlayerInGameId): PlayerDashboard {
        return PlayerDashboardFactory.create(currentGameState, playerId)
    }

    fun playCard(cardPlayed: Card): GameState {
        println("Player $currentPlayerId plays card $cardPlayed, current game state: $currentGameState")
        return PlayCard(
            PlayCardState(currentGameState, cardPlayed),
            PlayCardGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    fun pickCard(): GameState {
        println("Player $currentPlayerId picks a card, current game state: $currentGameState")
        return PickCard(
            PickCardState(currentGameState),
            PickCardGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    fun passTurn(): GameState {
        println("Player $currentPlayerId passes its turn, current game state: $currentGameState")
        return PassTurn(
            PassTurnState(currentGameState),
            PassTurnGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    fun startNewRound(cardDealerFactory: CardDealerFactory): GameState {
        println("Player $currentPlayerId starts a new round, current game state: $currentGameState")
        return StartNewRound(
            StartNewRoundState(currentGameState),
            StartNewRoundGameUpdater(currentGameState),
            cardDealerFactory
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    fun chooseCardsForExchange(playerId: PlayerInGameId, cards: Set<Card>): GameState {
        println("Choose exchange cards $cards for Player $currentPlayerId, current game state: $currentGameState")
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

    fun getCardsExchanges(): List<CardExchange> {
        if (currentGameState.phase != GamePhase.RoundIsBeginningWithCardExchange) {
            throw IllegalStateException("No card exchange is supposed to happen now.")
        }
        return currentGameState.players.values
            .mapNotNull { p -> CardExchangeComputer.getCardExchange(p.inGameId, p.rank, p.cardsInHand.toSet()) }
    }

    private fun logUpdatedGameState(gameState: GameState) {
        println("Updated game state: $gameState")
    }

    companion object {
        fun createNewGame(playersInfo: List<PlayerOnboardingInfo>, initialGameSetup: InitialGameSetup): GameState {
            println("Start new game, players:  $playersInfo, initial game setup: $initialGameSetup")
            return GameBootstrap.createNewGame(playersInfo, initialGameSetup)
        }
    }
}