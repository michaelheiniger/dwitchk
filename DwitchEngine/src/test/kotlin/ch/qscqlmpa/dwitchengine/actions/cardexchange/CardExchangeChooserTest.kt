package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardExchangeChooserTest : EngineTestBase() {

    @BeforeEach
    fun setupInitialGameState() {
        gameStateBuilder
            .setGamePhase(GamePhase.RoundIsBeginningWithCardExchange)
            .setCardsdOnTable(Card.Clubs4)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
    }

    @Test
    fun `Cards are taken from hands and put in cards for exchange`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs2, Card.Spades6, Card.Clubs10))

        launchTestForPlayer1(Card.Diamonds4, Card.HeartsAce)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertCardsInHandContainsExactly(Card.Clubs3)
            .assertCardsForExchangeContainsExactly(Card.Diamonds4, Card.HeartsAce)
    }

    @Test
    fun `Rule that President can choose any 2 cards`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.President, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.Asshole, listOf(Card.Clubs2, Card.Spades6, Card.Clubs10))

        launchTestForPlayer1(Card.Clubs3, Card.Diamonds4)
    }

    @Test
    fun `Rule that Vice-President can choose any 1 card`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.ViceAsshole, listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack))

        launchTestForPlayer1(Card.Clubs3)
    }

    @Test
    fun `Rule that Asshole must choose its 2 cards with highest value is enforced - success`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs2, Card.Spades6, Card.Clubs10))

        launchTestForPlayer1(Card.Diamonds4, Card.HeartsAce)
    }

    @Test
    fun `Rule that Asshole must choose its 2 cards with highest value is enforced - failure`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs2, Card.Spades6, Card.Clubs10))

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3, Card.Diamonds4) }
    }

    @Test
    fun `Rule that Vice-Asshole must choose its 1 card with highest value is enforced - success`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Waiting, Rank.ViceAsshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack))

        launchTestForPlayer1(Card.HeartsAce)
    }

    @Test
    fun `Rule that Vice-Asshole must choose its 1 card with highest value is enforced - failure`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Waiting, Rank.ViceAsshole, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack))

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3) }
    }

    @Test
    fun `Rule that Neutral players are not supposed to exchange any cards`() {
        gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Waiting, Rank.Neutral, listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce))
            .addPlayerToGame(player2, PlayerStatus.Playing, Rank.Asshole, listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack))
            .addPlayerToGame(player5, PlayerStatus.Waiting, Rank.ViceAsshole, listOf(Card.Clubs7, Card.Diamonds8, Card.Hearts10))

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3) }
    }

    private fun launchTestForPlayer1(vararg cardsForExchange: Card) {
        initialGameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(initialGameState).chooseCardsForExchange(player1.id, setOf(*cardsForExchange))
    }
}