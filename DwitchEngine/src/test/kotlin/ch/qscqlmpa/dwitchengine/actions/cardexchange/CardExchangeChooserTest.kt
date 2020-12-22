package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CardExchangeChooserTest : EngineTestBase() {

    @Test
    fun `Cards are taken from hands and put in cards for exchange`() {
        initialGameState = gameStateBuilder
            .setGamePhase(GamePhase.RoundIsBeginningWithCardExchange)
            .addPlayerToGame(player1, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerState.Waiting, Rank.President, listOf(Card.Clubs2, Card.Spades6, Card.Clubs10))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        launchTestForPlayer1(Card.Diamonds4, Card.HeartsAce)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertCardsInHandContainsExactly(Card.Clubs3)
            .assertCardsForExchangeContainsExactly(Card.Diamonds4, Card.HeartsAce)
    }

    @Test
    fun `Rule that Asshole must choose its 2 cards with highest value is enforced`() {
        initialGameState = gameStateBuilder
            .setGamePhase(GamePhase.RoundIsBeginningWithCardExchange)
            .addPlayerToGame(player1, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerState.Waiting, Rank.President, listOf(Card.Clubs2, Card.Spades6, Card.Clubs10))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3, Card.Diamonds4) }
    }

    @Test
    fun `Rule that Vice-Asshole must choose its 1 card with highest value is enforced`() {
        initialGameState = gameStateBuilder
            .setGamePhase(GamePhase.RoundIsBeginningWithCardExchange)
            .addPlayerToGame(player1, PlayerState.Waiting, Rank.ViceAsshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing))
            .addPlayerToGame(player3, PlayerState.Waiting, Rank.VicePresident, listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen))
            .addPlayerToGame(player4, PlayerState.Waiting, Rank.President, listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3) }
    }

    @Test
    fun `Rule that Neutral players are not supposed to exchange any cards`() {
        initialGameState = gameStateBuilder
            .setGamePhase(GamePhase.RoundIsBeginningWithCardExchange)
            .addPlayerToGame(player1, PlayerState.Waiting, Rank.Neutral, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerState.Playing, Rank.Asshole, listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing))
            .addPlayerToGame(player3, PlayerState.Waiting, Rank.VicePresident, listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen))
            .addPlayerToGame(player4, PlayerState.Waiting, Rank.President, listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack))
            .addPlayerToGame(player5, PlayerState.Waiting, Rank.ViceAsshole, listOf(Card.Clubs7, Card.Diamonds8, Card.Hearts10))
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
            .build()

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3) }
    }

    private fun launchTestForPlayer1(vararg cardsForExchange: Card) {
        gameStateUpdated = DwitchEngine(initialGameState).chooseCardsForExchange(player1.id, setOf(*cardsForExchange))
    }
}