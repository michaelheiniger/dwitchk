package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.junit.jupiter.api.Test

class PickCardTest : EngineTestBase() {

    @Test
    fun `Player picks a card`() {
        initialGameState = gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Clubs2))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        GameStateRobot(initialGameState)
            .assertCurrentPlayerId(player1Id)

        PlayerRobot(initialGameState, player1Id)
            .assertNumCardsInHand(1)

        launchPickCardTest()

        GameStateRobot(gameStateUpdated)
            .assertNumCardsOnTable(1) // Still one since no card played
            .assertCurrentPlayerId(player1Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertNumCardsInHand(2) // One more card: the card picked
            .assertCardsInHandContains(Card.Clubs5)
            .assertPlayerHasPickedCard()
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerHasNotPickedCard()
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    private fun launchPickCardTest() {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(gameState).pickCard()
    }
}
