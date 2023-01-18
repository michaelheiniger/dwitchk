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
        }

        @Test
        fun `The current player is a computer player`() {
            // Given
            gameStateBuilder
                .setPlayerCards(
                    p2Id,
                    Card.Clubs2,
                    Card.Spades3,
                )
                .setCurrentPlayer(p2Id)

            // When
            val actions = launchTest()

            // Then
            assertThat(actions.size).isEqualTo(1)
            assertThat(actions[0].dwitchId).isEqualTo(p2Id)
        }

        @Test
        fun `The current player is not a computer player`() {
            // Given
            gameStateBuilder
                .setPlayerStatus(p2Id, DwitchPlayerStatus.Waiting)
                .setPlayerStatus(p1Id, DwitchPlayerStatus.Playing)
                .setCurrentPlayer(p1Id)

            // When
            val actions = launchTest()

            // Then
            assertThat(actions).isEmpty()
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
        }

        @Test
        fun `Each player who needs to exchange cards selects cards`() {
            // Given
            gameStateBuilder
                .setGamePhase(DwitchGamePhase.CardExchange)
                .setCurrentPlayer(p2Id)
                .updatePlayer(p1Id, DwitchRank.President, Card.Clubs2, Card.Clubs3, Card.Clubs10, Card.ClubsAce)
                .updatePlayer(p2Id, DwitchRank.VicePresident, Card.Spades2, Card.Spades3, Card.Spades10, Card.SpadesAce)
                .updatePlayer(p3Id, DwitchRank.Neutral, Card.Clubs6, Card.Clubs7)
                .updatePlayer(p4Id, DwitchRank.ViceAsshole, Card.Hearts2, Card.Hearts3, Card.Hearts10, Card.HeartsAce)
                .updatePlayer(p5Id, DwitchRank.Asshole, Card.Diamonds2, Card.Diamonds3, Card.Diamonds10, Card.DiamondsAce)

            // When
            val actions = launchTest()

            // Then
            assertThat(actions.size).isEqualTo(3) // 5 -1 (Neutral never exchanges cards) -1 (human player)
        }
    }

    private fun launchTest(): List<ComputerPlayerActionResult> {
        val gameState = gameStateBuilder.build()
        computerPlayerEngine =
            ComputerPlayerEngineImpl(DwitchEngineImpl(gameState), computerPlayersId, ComputerReflexionTime.ZERO)
        return computerPlayerEngine.handleComputerPlayerAction()
    }
}
