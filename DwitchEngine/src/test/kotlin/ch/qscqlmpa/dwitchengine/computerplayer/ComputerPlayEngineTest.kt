package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ComputerPlayEngineTest : EngineTestBase() {

    private lateinit var engine: ComputerPlayEngine

    @BeforeEach
    fun setupGameState() {
        gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
            .addPlayerToGame(p1, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Clubs7))
            .addPlayerToGame(p2, DwitchPlayerStatus.Playing, DwitchRank.VicePresident, emptyList())
            .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Hearts7))
            .addPlayerToGame(p4, DwitchPlayerStatus.Waiting, DwitchRank.ViceAsshole, listOf(Card.Spades7))
            .addPlayerToGame(p5, DwitchPlayerStatus.Waiting, DwitchRank.Asshole, listOf(Card.Diamonds7))
            .setCurrentPlayer(p2Id)
    }

    @Test
    fun `table empty - player should play card(s) with highest multiplicity and then lowest value (heuristic)`() {
        // Given
        gameStateBuilder
            .setPlayerCards(
                p2Id,
                Card.Clubs2,
                Card.Spades3,
                Card.Clubs3,
                Card.Hearts6,
                Card.Spades6,
                Card.DiamondsAce
            )
            .setCardsdOnTable() // Table empty

        // When
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()!!.cards)
            .containsExactlyInAnyOrder(Card.Clubs3, Card.Spades3)
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `table not empty - player should play card(s) with lowest value possible (heuristic)`() {
        // Given
        gameStateBuilder
            .setPlayerCards(
                p2Id,
                Card.Clubs2,
                Card.Spades3,
                Card.Clubs3,
                Card.Hearts6,
                Card.Spades6,
                Card.DiamondsAce,
                Card.HeartsAce
            )
            .setCardsdOnTable(PlayedCards(Card.Hearts3, Card.Diamonds3), PlayedCards(Card.Hearts5, Card.Spades5))

        // When
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()!!.cards).containsExactlyInAnyOrder(
            Card.Hearts6,
            Card.Spades6
        )
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `player should pass when the last card played is the first Jack of the round (special rule)`() {
        // Given
        gameStateBuilder
            .setPlayerCards(p2Id, Card.Clubs2, Card.Spades3, Card.Hearts6, Card.DiamondsAce)
            .setCardsdOnTable(PlayedCards(Card.Spades10), PlayedCards(Card.HeartsJack))
            .setCardGraveyard() // No other Jack has been played in this round

        // When
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()).isEqualTo(PlayedCards(Card.HeartsJack)) // Hasn't changed
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.TurnPassed)
    }

    @Test
    fun `player can play on a Jack when at least one other Jack has been played in the round`() {
        // Given
        gameStateBuilder
            .setPlayerCards(p2Id, Card.Clubs2, Card.Spades3, Card.Hearts6, Card.DiamondsAce)
            .setCardsdOnTable(PlayedCards(Card.Spades10), PlayedCards(Card.HeartsJack))
            .setCardGraveyard(PlayedCards(Card.SpadesJack), PlayedCards(Card.DiamondsQueen))

        // When
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()).isEqualTo(PlayedCards(Card.DiamondsAce))
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `table empty - player should not keep a joker as last card to play (special rule)`() {
        // Given
        gameStateBuilder
            .setPlayerCards(p2Id, Card.Clubs2, Card.Diamonds2, Card.DiamondsAce, Card.HeartsAce)
            .setCardsdOnTable() // Table empty

        // When a card with a lower value than a joker could be played but it's the last non-joker card in hand,
        // play the jokers first
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()).isNull() // Table is cleared because of joker
        Assertions.assertThat(action.updatedGameState.cardsInGraveyard).containsExactly(
            PlayedCards(Card.Clubs2, Card.Diamonds2)
        )
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `table not empty - player should not keep a joker as last card to play (special rule)`() {
        // Given
        gameStateBuilder
            .setPlayerCards(p2Id, Card.Clubs2, Card.DiamondsAce)
            .setCardsdOnTable(PlayedCards(Card.Spades10), PlayedCards(Card.HeartsQueen))

        // When a card with a lower value than a joker could be played but it's the last non-joker card in hand,
        // play the jokers first
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()).isNull() // Table is cleared because of joker
        Assertions.assertThat(action.updatedGameState.cardsInGraveyard).containsExactly(
            PlayedCards(Card.Spades10),
            PlayedCards(Card.HeartsQueen),
            PlayedCards(Card.Clubs2)
        )
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `player should pass when it has jokers and only one last card to play but cannot play joker (special rule)`() {
        // Given
        gameStateBuilder
            .setPlayerCards(p2Id, Card.Clubs2, Card.DiamondsAce, Card.HeartsAce)
            .setCardsdOnTable(PlayedCards(Card.Spades10, Card.Hearts10))

        // When a card with a lower value than a joker could be played and the jokers cannot (due to too low multiplicity)
        // then pass to prevent breaking special rule
        val action = launchTest()

        // Then
        Assertions.assertThat(action.updatedGameState.lastCardsPlayed()).isEqualTo(PlayedCards(Card.Spades10, Card.Hearts10))
        Assertions.assertThat(action.updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.TurnPassed)
    }

    private fun launchTest(): ComputerPlayerActionResult {
        val gameState = gameStateBuilder.build()
        engine = ComputerPlayEngine(DwitchEngineImpl(gameState), p2Id)
        return engine.play()
    }
}