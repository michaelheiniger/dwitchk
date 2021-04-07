package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.junit.jupiter.api.Test

class PassTurnTest : EngineTestBase() {

    @Test
    fun `Player passes its turn`() {
        initialGameState = gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3), hasPickedCard = true)
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Clubs2))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Clubs4))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        launchPassTurnTest()

        GameStateRobot(gameStateUpdated)
            .assertCurrentPlayerId(player2Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertNumCardsInHand(1) // Hasn't changed
            .assertPlayerState(DwitchPlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `When only one Waiting player, table is cleared and waiting player becomes Playing`() {
        initialGameState = gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3), hasPickedCard = true)
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Clubs4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Clubs5))
            .addPlayerToGame(player4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.Clubs6))
            .setCardsdOnTable(Card.Clubs7)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        launchPassTurnTest()

        GameStateRobot(gameStateUpdated)
            .assertCurrentPlayerId(player3Id)
            .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    private fun launchPassTurnTest() {
        gameStateUpdated = DwitchEngineImpl(initialGameState).passTurn()
    }
}
