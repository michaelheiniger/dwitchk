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
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboardFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerInfo
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
            PlayCardGameUpdater(currentGameState, cardPlayed)
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

    fun addCardsForExchange(playerId: PlayerInGameId, cards: Set<Card>): GameState {
        println("Add exchange cards $cards for Player $currentPlayerId, current game state: $currentGameState")
        val gameStateUpdated = AddCardsForExchange(
            AddCardsForExchangeState(currentGameState, playerId, cards),
            AddCardsForExchangeGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)

        return CardExchangePerformer(
            CardExchangeState(gameStateUpdated),
            CardExchangeGameUpdater(gameStateUpdated)
        ).performCardExchange()
            .also(this::logUpdatedGameState)
    }

    fun getCardsExchanges(): List<Pair<PlayerInGameId, CardExchange>> {
        return currentGameState.players.values
            .mapNotNull { p ->
                val cardExchange = CardExchangeComputer.getCardExchange(p.inGameId, p.rank, p.cardsInHand)
                if (cardExchange != null) Pair(p.inGameId, cardExchange) else null
            }
    }

    private fun logUpdatedGameState(gameState: GameState) {
        println("Updated game state: $gameState")
    }

    companion object {
        fun createNewGame(playersInfo: List<PlayerInfo>, initialGameSetup: InitialGameSetup): GameState {
            println("Start new game, players:  $playersInfo, initial game setup: $initialGameSetup")
            return GameBootstrap.createNewGame(playersInfo, initialGameSetup)
        }
    }
}