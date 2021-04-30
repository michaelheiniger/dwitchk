package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ComputerPlayerEngineTest : EngineTestBase() {

    private lateinit var computerPlayerEngine: ComputerPlayerEngineImpl

    // Player1 is human, the others are computer managed
    private val computerPlayersId = setOf(p2Id, p3Id, p4Id, p5Id)

    @Nested
    inner class RoundIsBeginningAndRoundIsOnGoing {

        @BeforeEach
        fun setup() {
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
        fun `player should play card with lowest value possible (heuristic)`() {
            // Given
            val cardExpectedToBePlayed = Card.Hearts6
            gameStateBuilder
                .setPlayerCards(p2Id, Card.Clubs2, Card.Spades3, cardExpectedToBePlayed, Card.DiamondsAce)
                .setCardsdOnTable(Card.SpadesKing, Card.Hearts5)

            // When
            val actions = launchTest()

            // Then
            assertThat(actions.size).isEqualTo(1)
            assertThat(actions[0].dwitchId).isEqualTo(p2Id)
            assertThat(actions[0].updatedGameState.lastCardOnTable()).isEqualTo(cardExpectedToBePlayed)
            assertThat(actions[0].updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `player should pass when the last card played is the first Jack of the round (special rule)`() {
            // Given
            gameStateBuilder
                .setPlayerCards(p2Id, Card.Clubs2, Card.Spades3, Card.Hearts6, Card.DiamondsAce)
                .setCardsdOnTable(Card.Spades10, Card.HeartsJack)
                .setCardGraveyard() // No other Jack has been played in this round

            // When
            val actions = launchTest()

            // Then
            assertThat(actions.size).isEqualTo(1)
            assertThat(actions[0].dwitchId).isEqualTo(p2Id)
            assertThat(actions[0].updatedGameState.lastCardOnTable()).isEqualTo(Card.HeartsJack) // Hasn't changed
            assertThat(actions[0].updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.TurnPassed)
        }

        @Test
        fun `player can play on a Jack when at least one other Jack has been played in the round`() {
            // Given
            gameStateBuilder
                .setPlayerCards(p2Id, Card.Clubs2, Card.Spades3, Card.Hearts6, Card.DiamondsAce)
                .setCardsdOnTable(Card.Spades10, Card.HeartsJack)
                .setCardGraveyard(Card.SpadesJack, Card.DiamondsQueen)

            // When
            val actions = launchTest()

            // Then
            assertThat(actions.size).isEqualTo(1)
            assertThat(actions[0].dwitchId).isEqualTo(p2Id)
            assertThat(actions[0].updatedGameState.lastCardOnTable()).isEqualTo(Card.DiamondsAce)
            assertThat(actions[0].updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `player should not keep a joker as last card to play (special rule)`() {
            // Given
            gameStateBuilder
                .setPlayerCards(p2Id, Card.Clubs2, Card.DiamondsAce)
                .setCardsdOnTable(Card.Spades10, Card.HeartsQueen)

            // When a card with a lower value than a joker could be played but it's the last non-joker card in hand,
            // play the jokers first
            val actions = launchTest()

            // Then
            assertThat(actions.size).isEqualTo(1)
            assertThat(actions[0].dwitchId).isEqualTo(p2Id)
            assertThat(actions[0].updatedGameState.lastCardOnTable()).isNull() // Table is cleared because of joker
            assertThat(actions[0].updatedGameState.cardsInGraveyard).containsExactly(Card.Spades10, Card.HeartsQueen, Card.Clubs2)
            assertThat(actions[0].updatedGameState.player(p2Id).status).isEqualTo(DwitchPlayerStatus.Playing)
        }
    }

    @Nested
    inner class CardExchange {

        @BeforeEach
        fun setup() {
            gameStateBuilder
                .setGamePhase(DwitchGamePhase.CardExchange)
                .setCurrentPlayer(p2Id)
                .addPlayerToGame(p1, DwitchPlayerStatus.Done, DwitchRank.Neutral, emptyList())
                .addPlayerToGame(p2, DwitchPlayerStatus.Done, DwitchRank.President, emptyList())
                .addPlayerToGame(p3, DwitchPlayerStatus.Done, DwitchRank.VicePresident, emptyList())
                .addPlayerToGame(p4, DwitchPlayerStatus.Done, DwitchRank.ViceAsshole, emptyList())
                .addPlayerToGame(p5, DwitchPlayerStatus.Done, DwitchRank.Asshole, emptyList())
                .setCurrentPlayer(p2Id)
        }

        @Test
        fun `all players who need to perform a card exchange return an action`() {
            // Given
            gameStateBuilder
                .updatePlayer(p1Id, DwitchRank.President, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.VicePresident, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When 3 of the 4 computer players have a non-neutral rank
            val actions = launchTest()

            // Then 3 computer players choose cards to exchange, the remaining computer player is Neutral and hence does nothing
            assertThat(actions.size).isEqualTo(3)
        }

        @Test
        fun `asshole has to pick the two cards with the highest value (game rule)`() {
            // Given
            gameStateBuilder
                .updatePlayer(p1Id, DwitchRank.President, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.VicePresident, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When
            val actions = launchTest()

            // Then
            val actionOfP5 = actions.find { a -> a.dwitchId == p5Id }!!
            assertThat(actionOfP5.updatedGameState.players.getValue(p5Id).cardsForExchange)
                .containsOnly(Card.Diamonds2, Card.DiamondsAce)
        }

        @Test
        fun `vice-asshole has to pick the card with the highest value (game rule)`() {
            // Given
            gameStateBuilder
                .updatePlayer(p1Id, DwitchRank.President, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.VicePresident, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When
            val actions = launchTest()

            // Then
            val actionOfP4 = actions.find { a -> a.dwitchId == p4Id }!!
            assertThat(actionOfP4.updatedGameState.players.getValue(p4Id).cardsForExchange).containsOnly(Card.Hearts2)
        }

        @Test
        fun `vice-president should pick the card with the lowest value (heuristic)`() {
            // Given
            gameStateBuilder
                .updatePlayer(p1Id, DwitchRank.President, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.VicePresident, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When
            val actions = launchTest()

            // Then
            val actionOfP2 = actions.find { a -> a.dwitchId == p2Id }!!
            assertThat(actionOfP2.updatedGameState.players.getValue(p2Id).cardsForExchange).containsOnly(Card.Spades3)
        }

        @Test
        fun `president should pick the two cards with the lowest value (heuristic)`() {
            // Given
            gameStateBuilder
                .updatePlayer(p1Id, DwitchRank.VicePresident, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.President, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When
            val actions = launchTest()

            // Then
            val actionOfP2 = actions.find { a -> a.dwitchId == p2Id }!!
            assertThat(actionOfP2.updatedGameState.players.getValue(p2Id).cardsForExchange)
                .containsOnly(Card.Spades3, Card.Spades10)
        }

        @Test
        fun `neutral player have nothing to do`() {
            // Given
            gameStateBuilder
                .updatePlayer(p1Id, DwitchRank.VicePresident, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.President, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When
            val actions = launchTest()

            // Then
            val actionOfP3 = actions.find { a -> a.dwitchId == p3Id }
            assertThat(actionOfP3).isNull()
        }
    }

    private fun launchTest(): List<ComputerPlayerActionResult> {
        val gameState = gameStateBuilder.build()
        computerPlayerEngine = ComputerPlayerEngineImpl(DwitchEngineImpl(gameState), computerPlayersId)
        return computerPlayerEngine.handleComputerPlayerAction()
    }
}