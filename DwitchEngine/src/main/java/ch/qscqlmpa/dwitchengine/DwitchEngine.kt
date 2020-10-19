package ch.qscqlmpa.dwitchengine

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
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDashboard
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerInfo

/* Important rule: As soon as the last player to have played a card has to play again
(i.e. a table round has happened but no one could play a card, regardless of the reason: dwitched, pass, ...),
the table stack is removed and the player can play any card.
 */

/**
 * The Engine is executed by the current player. Hence the assumption is that the current player is the local player.
 */
class DwitchEngine(gameInfo: GameInfo) {

    private val localPlayerId = gameInfo.localPlayerId
    private val currentGameState = gameInfo.gameState.copy(localPlayerId = localPlayerId)

    fun getPlayerDashboard(): PlayerDashboard {
        return PlayerDashboardFactory.create(currentGameState)
    }

    fun playCard(cardPlayed: Card): GameInfo {
//        Timber.i("Player $localPlayerId plays card $cardPlayed, current game state: $currentGameState")//FIXME
        return PlayCard(
                PlayCardState(currentGameState, cardPlayed),
                PlayCardGameUpdater(currentGameState, cardPlayed)
        ).getUpdatedGameState()
                .also(this::logUpdatedGameState)
    }

    fun pickCard(): GameInfo {
//        Timber.i("Player $localPlayerId picks a card, current game state: $currentGameState")//FIXME
        return PickCard(
                PickCardState(currentGameState),
                PickCardGameUpdater(currentGameState)
        ).getUpdatedGameState()
                .also(this::logUpdatedGameState)
    }

    fun passTurn(): GameInfo {
//        Timber.i("Player $localPlayerId passes its turn, current game state: $currentGameState")//FIXME
        return PassTurn(
                PassTurnState(currentGameState),
                PassTurnGameUpdater(currentGameState)
        ).getUpdatedGameState()
                .also(this::logUpdatedGameState)
    }

    fun startNewRound(cardDealerFactory: CardDealerFactory): GameInfo {
//        Timber.i("Player $localPlayerId starts a new round, current game state: $currentGameState")//FIXME
        return StartNewRound(
                StartNewRoundState(currentGameState),
                StartNewRoundGameUpdater(currentGameState),
                cardDealerFactory
        ).getUpdateGameState()
                .also(this::logUpdatedGameState)
    }

    private fun logUpdatedGameState(gameInfo: GameInfo) {
//        Timber.i("Updated game state: ${gameInfo.gameState}")//FIXME
    }

    companion object {
        fun createNewGame(playersInfo: List<PlayerInfo>, localPlayerId: PlayerInGameId, initialGameSetup: InitialGameSetup): GameState {
//            Timber.i("Start new game, players:  $playersInfo, initial game setup: $initialGameSetup")//FIXME
            return GameBootstrap.createNewGame(playersInfo, localPlayerId, initialGameSetup)
        }
    }
}