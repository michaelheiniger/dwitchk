package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchPlayerAction
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PassTurnTest : EngineTestBase() {

    @Nested
    @DisplayName("When passing, the next 'Waiting' player (in order) becomes 'Playing'")
    inner class NextWaitingPlayerBecomesPlaying {

        @Test
        fun `When exactly one player is 'Waiting', the table is cleared and all 'TurnPassed' players are reset to 'Waiting'`() {
            initialGameState = gameStateBuilder
                .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Clubs4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Clubs5))
                .addPlayerToGame(p4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.Clubs6))
                .setCardsdOnTable(PlayedCards(Card.Clubs7))
                .setCurrentPlayer(p1Id)
                .build()

            launchPassTurnTest()

            GameStateRobot(gameStateUpdated)
                .assertCurrentPlayerId(p3Id)
                .assertTableIsEmpty()
                .assertLastPlayerAction(DwitchPlayerAction.PassTurn(playerId = p1Id, clearsTable = true))

            PlayerRobot(gameStateUpdated, p1Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertNumCardsInHand(1) // Hasn't changed

            PlayerRobot(gameStateUpdated, p2Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)

            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Playing)

            PlayerRobot(gameStateUpdated, p4Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `When exactly one player is 'Waiting' - 2 players`() {
            initialGameState = gameStateBuilder
                .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Clubs4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Done, DwitchRank.Neutral, listOf(Card.Clubs5))
                .setCardsdOnTable(PlayedCards(Card.Clubs7))
                .setCurrentPlayer(p1Id)
                .build()

            launchPassTurnTest()

            GameStateRobot(gameStateUpdated)
                .assertCurrentPlayerId(p2Id)
                .assertTableIsEmpty()
                .assertLastPlayerAction(DwitchPlayerAction.PassTurn(playerId = p1Id, clearsTable = true))

            PlayerRobot(gameStateUpdated, p1Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertNumCardsInHand(1) // Hasn't changed
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
        }

        @Test
        fun `When more than one player is 'Waiting', the table is not cleared and current player becomes 'TurnPassed'`() {
            initialGameState = gameStateBuilder
                .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Clubs4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Clubs5))
                .addPlayerToGame(p4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Clubs6))
                .setCardsdOnTable(PlayedCards(Card.Clubs7))
                .setCurrentPlayer(p1Id)
                .build()

            launchPassTurnTest()

            GameStateRobot(gameStateUpdated)
                .assertCurrentPlayerId(p3Id)
                .assertTableContains(PlayedCards(Card.Clubs7))
                .assertLastPlayerAction(DwitchPlayerAction.PassTurn(playerId = p1Id, clearsTable = false))

            PlayerRobot(gameStateUpdated, p1Id)
                .assertPlayerState(DwitchPlayerStatus.TurnPassed)
                .assertNumCardsInHand(1) // Hasn't changed
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.TurnPassed)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }
    }

    private fun launchPassTurnTest() {
        gameStateUpdated = DwitchEngineImpl(initialGameState).passTurn()
    }
}
