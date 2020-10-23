package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.Test

class PassTurnTest : EngineTestBase() {

    @Test
    fun `Player passes its turn`() {
        initialGameState = gameStateBuilder
                .setGamePhase(GamePhase.RoundIsOnGoing)
                .addPlayerToGame(player1, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs3), hasPickedCard = true)
                .addPlayerToGame(player2, PlayerState.Waiting, Rank.Neutral, listOf(Card.Clubs2))
                .addPlayerToGame(player3, PlayerState.Waiting, Rank.President, listOf(Card.Clubs4))
                .setCardsdOnTable(Card.Clubs4)
                .setLocalPlayer(player1Id)
                .setCurrentPlayer(player1Id)
                .build()

        launchPassTurnTest()

        GameStateRobot(gameStateUpdated)
                .assertCurrentPlayerId(player2Id)

        PlayerRobot(gameStateUpdated, player1Id)
                .assertNumCardsInHand(1) // Hasn't changed
                .assertPlayerState(PlayerState.TurnPassed)

        PlayerRobot(gameStateUpdated, player2Id)
                .assertPlayerState(PlayerState.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
                .assertPlayerState(PlayerState.Waiting)

    }

    @Test
    fun `When only one Waiting player, table is cleared and waiting player becomes Playing`() {
        initialGameState = gameStateBuilder
                .setGamePhase(GamePhase.RoundIsOnGoing)
                .addPlayerToGame(player1, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs3), hasPickedCard = true)
                .addPlayerToGame(player2, PlayerState.TurnPassed, Rank.ViceAsshole, listOf(Card.Clubs4))
                .addPlayerToGame(player3, PlayerState.Waiting, Rank.VicePresident, listOf(Card.Clubs5))
                .addPlayerToGame(player4, PlayerState.TurnPassed, Rank.President, listOf(Card.Clubs6))
                .setCardsdOnTable(Card.Clubs7)
                .setLocalPlayer(player1Id)
                .setCurrentPlayer(player1Id)
                .build()

        launchPassTurnTest()

        GameStateRobot(gameStateUpdated)
                .assertCurrentPlayerId(player3Id)
                .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
                .assertPlayerState(PlayerState.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
                .assertPlayerState(PlayerState.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
                .assertPlayerState(PlayerState.Playing)

        PlayerRobot(gameStateUpdated, player4Id)
                .assertPlayerState(PlayerState.Waiting)
    }

    private fun launchPassTurnTest() {
        gameStateUpdated = DwitchEngine(initialGameState).passTurn()
    }
}