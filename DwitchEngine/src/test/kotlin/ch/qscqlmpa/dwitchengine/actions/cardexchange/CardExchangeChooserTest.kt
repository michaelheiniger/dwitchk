package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardExchangeChooserTest : EngineTestBase() {

    @BeforeEach
    fun setupInitialGameState() {
        gameStateBuilder
            .setGamePhase(DwitchGamePhase.CardExchange)
            .setCardsdOnTable(PlayedCards(Card.Clubs8))
            .setCurrentPlayer(p1Id)
    }

    @Test
    fun `Cards are taken from hands and put in cards for exchange`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs2, Card.Spades6, Card.Clubs10)
            ).setCurrentPlayer(p1.id)

        launchTestForPlayer1(Card.Diamonds4, Card.HeartsAce)

        PlayerRobot(gameStateUpdated, p1Id)
            .assertCardsInHandContainsExactly(Card.Clubs3)
            .assertCardsForExchangeContainsExactly(Card.Diamonds4, Card.HeartsAce)
    }

    @Test
    fun `Rule that President can choose any 2 cards`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Playing,
                DwitchRank.President,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Waiting,
                DwitchRank.Asshole,
                listOf(Card.Clubs2, Card.Spades6, Card.Clubs10)
            ).setCurrentPlayer(p1.id)

        launchTestForPlayer1(Card.Clubs3, Card.Diamonds4)
    }

    @Test
    fun `Rule that Vice-President can choose any 1 card`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Waiting,
                DwitchRank.VicePresident,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing)
            )
            .addPlayerToGame(
                p3,
                DwitchPlayerStatus.Waiting,
                DwitchRank.ViceAsshole,
                listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen)
            )
            .addPlayerToGame(
                p4,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack)
            ).setCurrentPlayer(p2.id)

        launchTestForPlayer1(Card.Clubs3)
    }

    @Test
    fun `Rule that Asshole must choose its 2 cards with highest value is enforced - success`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs2, Card.Spades6, Card.Clubs10)
            ).setCurrentPlayer(p1.id)

        launchTestForPlayer1(Card.Diamonds4, Card.HeartsAce)
    }

    @Test
    fun `Rule that Asshole must choose its 2 cards with highest value is enforced - failure`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs2, Card.Spades6, Card.Clubs10)
            ).setCurrentPlayer(p1.id)

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3, Card.Diamonds4) }
    }

    @Test
    fun `Rule that Vice-Asshole must choose its 1 card with highest value is enforced - success`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Waiting,
                DwitchRank.ViceAsshole,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing)
            )
            .addPlayerToGame(
                p3,
                DwitchPlayerStatus.Waiting,
                DwitchRank.VicePresident,
                listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen)
            )
            .addPlayerToGame(
                p4,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack)
            ).setCurrentPlayer(p2.id)

        launchTestForPlayer1(Card.HeartsAce)
    }

    @Test
    fun `Rule that Vice-Asshole must choose its 1 card with highest value is enforced - failure`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Waiting,
                DwitchRank.ViceAsshole,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing)
            )
            .addPlayerToGame(
                p3,
                DwitchPlayerStatus.Waiting,
                DwitchRank.VicePresident,
                listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen)
            )
            .addPlayerToGame(
                p4,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack)
            ).setCurrentPlayer(p2.id)

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3) }
    }

    @Test
    fun `Rule that Neutral players are not supposed to exchange any cards`() {
        gameStateBuilder
            .addPlayerToGame(
                p1,
                DwitchPlayerStatus.Waiting,
                DwitchRank.Neutral,
                listOf(Card.Clubs3, Card.Diamonds4, Card.HeartsAce)
            )
            .addPlayerToGame(
                p2,
                DwitchPlayerStatus.Playing,
                DwitchRank.Asshole,
                listOf(Card.Clubs4, Card.Diamonds5, Card.HeartsKing)
            )
            .addPlayerToGame(
                p3,
                DwitchPlayerStatus.Waiting,
                DwitchRank.VicePresident,
                listOf(Card.Clubs5, Card.Diamonds6, Card.HeartsQueen)
            )
            .addPlayerToGame(
                p4,
                DwitchPlayerStatus.Waiting,
                DwitchRank.President,
                listOf(Card.Clubs6, Card.Diamonds7, Card.HeartsJack)
            )
            .addPlayerToGame(
                p5,
                DwitchPlayerStatus.Waiting,
                DwitchRank.ViceAsshole,
                listOf(Card.Clubs7, Card.Diamonds8, Card.Hearts10)
            ).setCurrentPlayer(p1.id)

        Assertions.assertThrows(IllegalArgumentException::class.java) { launchTestForPlayer1(Card.Clubs3) }
    }

    private fun launchTestForPlayer1(vararg cardsForExchange: Card) {
        initialGameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(initialGameState).chooseCardsForExchange(p1.id, setOf(*cardsForExchange))
    }
}
