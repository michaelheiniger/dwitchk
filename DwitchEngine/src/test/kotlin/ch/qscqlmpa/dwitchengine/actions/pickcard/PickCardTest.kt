package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.Test

class PickCardTest : EngineTestBase() {

    @Test
    fun `Player picks a card`() {
        initialGameState = gameStateBuilder
                .setGamePhase(GamePhase.RoundIsOnGoing)
                .addPlayerToGame(player1, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs3))
                .addPlayerToGame(player2, PlayerState.Waiting, Rank.President, listOf(Card.Clubs2))
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
                .assertPlayerState(PlayerState.Playing)

        PlayerRobot(gameStateUpdated, player2Id)
                .assertPlayerHasNotPickedCard()
                .assertPlayerState(PlayerState.Waiting)
    }

    private fun launchPickCardTest() {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngine(gameState).pickCard()
    }
}