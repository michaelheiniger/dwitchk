package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.Test

class PassTurnTest : EngineTestBase() {

    @Test
    fun `Player passes its turn`() {
        initialGameState = gameStateBuilder
            .setGamePhase(GamePhase.RoundIsOnGoing)
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs3), hasPickedCard = true)
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.Neutral, listOf(Card.Clubs2))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs4))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        launchPassTurnTest()

        GameStateRobot(gameStateUpdated)
            .assertCurrentPlayerId(player2Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertNumCardsInHand(1) // Hasn't changed
            .assertPlayerState(PlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `When only one Waiting player, table is cleared and waiting player becomes Playing`() {
        initialGameState = gameStateBuilder
            .setGamePhase(GamePhase.RoundIsOnGoing)
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs3), hasPickedCard = true)
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Clubs4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Clubs5))
            .addPlayerToGame(player4, PlayerStatus.TurnPassed, Rank.President, listOf(Card.Clubs6))
            .setCardsdOnTable(Card.Clubs7)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        launchPassTurnTest()

        GameStateRobot(gameStateUpdated)
            .assertCurrentPlayerId(player3Id)
            .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    private fun launchPassTurnTest() {
        gameStateUpdated = DwitchEngineImpl(initialGameState).passTurn()
    }
}
