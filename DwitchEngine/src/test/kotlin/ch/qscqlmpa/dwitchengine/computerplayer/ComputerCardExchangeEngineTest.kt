package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ComputerCardExchangeEngineTest : EngineTestBase() {

    private lateinit var engine: ComputerCardExchangeEngine

    // Player1 is human, the others are computer managed
    private val computerPlayersId = setOf(p2Id, p3Id, p4Id, p5Id)

    @BeforeEach
    fun setupGameState() {
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
        Assertions.assertThat(actions.size).isEqualTo(3)
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
        Assertions.assertThat(actionOfP5.updatedGameState.players.getValue(p5Id).cardsForExchange)
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
        Assertions.assertThat(actionOfP4.updatedGameState.players.getValue(p4Id).cardsForExchange).containsOnly(Card.Hearts2)
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
        Assertions.assertThat(actionOfP2.updatedGameState.players.getValue(p2Id).cardsForExchange).containsOnly(Card.Spades3)
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
        Assertions.assertThat(actionOfP2.updatedGameState.players.getValue(p2Id).cardsForExchange)
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
        Assertions.assertThat(actionOfP3).isNull()
    }

    private fun launchTest(): List<ComputerPlayerActionResult> {
        val gameState = gameStateBuilder.build()
        engine = ComputerCardExchangeEngine(DwitchEngineImpl(gameState), computerPlayersId)
        return engine.performCardExchangeIfNeeded()
    }
}
