package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlayCardTest : EngineTestBase() {

    @BeforeEach
    override fun setup() {
        super.setup()
        gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
            .setCurrentPlayer(p1Id)
    }

    @Nested
    inner class PlayerPlaysACard {
        @Test
        fun `Player plays a card with too low a value, an error is triggered since this should not happen`() {
            val cardPlayed = Card.Clubs3 // Lower value than last card on table: invalid
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
                .setCardsdOnTable(Card.Spades4)
                .build()

            Assertions.assertThrows(IllegalArgumentException::class.java) { launchPlayCardTest(cardPlayed) }
        }

        @Test
        fun `Player plays a card that is not in its hand, an error is triggered since this should not happen`() {
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(Card.Clubs3))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
                .setCardsdOnTable(Card.Spades4)
                .build()

            Assertions.assertThrows(IllegalArgumentException::class.java) { launchPlayCardTest(Card.Clubs10) }
        }

        @Test
        fun `Player plays a card with a higher value than the last card played`() {
            val cardPlayed = Card.Clubs7
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Spades5))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
                .setCardsdOnTable(Card.Clubs3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertNumCardsOnTable(2)
                .assertCardsOnTableContains(Card.Clubs3, cardPlayed) // Card has been added to table

            PlayerRobot(gameStateUpdated, p1Id)
                .assertNumCardsInHand(1)
                .assertCardsInHandContains(Card.Spades5) // Card has been removed from hand
                .assertPlayerState(DwitchPlayerStatus.Waiting)

            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
        }

        @Test
        fun `Player dwitches next player by playing a card with the same value as the last card on table`() {
            val cardPlayed = Card.Clubs4 // Same value as last card on the table
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Spades5))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
                .setCardsdOnTable(Card.Spades4)
                .build()

            assertThat(initialGameState.lastCardOnTable()!!.value()).isEqualTo(cardPlayed.value())

            launchPlayCardTest(cardPlayed)

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Waiting)

            PlayerRobot(gameStateUpdated, p2Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertPlayerIsDwitched()

            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Playing)
                .assertPlayerIsNotDwitched()
        }

        @Test
        fun `Player dwitches next waiting player and there is no other waiting player`() {
            val cardPlayed = Card.Clubs3
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.HeartsJack))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated).assertTableIsEmpty()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertPlayerIsDwitched()
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `Player dwitches next waiting player and there is at least one other waiting player`() {
            val cardPlayed = Card.Clubs3
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated).assertTableContains(cardPlayed)

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.TurnPassed)
            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertPlayerIsDwitched()
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Playing)
        }

        @Test
        fun `Player plays a card and does not dwitch next waiting player`() {
            val cardPlayed = Card.Clubs4
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated).assertTableContains(cardPlayed)

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.TurnPassed)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `Player plays a joker`() {
            val cardPlayed = Card.Clubs2
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.HeartsJack))
                .setCardsdOnTable(Card.Spades4)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated).assertTableIsEmpty()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }
    }

    @Nested
    inner class PlayerPlaysItsLastCard {
        @Test
        fun `Player plays its last card`() {
            val cardPlayed = Card.Clubs4
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertPlayerIsDoneForRound(p1Id)
                .assertPlayerHasNotFinishedWithJoker(p1Id)
                .assertRoundIsNotOver()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `Player plays its last card and dwitches the next player`() {
            val cardPlayed = Card.Clubs3 // Same value as last card on the table
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertPlayerIsDoneForRound(p1Id)
                .assertPlayerHasNotFinishedWithJoker(p1Id)
                .assertRoundIsNotOver()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertPlayerIsDwitched()
            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Playing)
                .assertPlayerIsNotDwitched()
        }

        @Test
        fun `Player plays its last card and dwitches next waiting player and there is no other waiting player`() {
            val cardPlayed = Card.Clubs3
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.HeartsJack))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertPlayerIsDoneForRound(p1Id)
                .assertPlayerHasNotFinishedWithJoker(p1Id)
                .assertTableIsEmpty()
                .assertGameEvent(DwitchGameEvent.TableHasBeenCleared(cardPlayed))
                .assertRoundIsNotOver()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertPlayerIsDwitched()
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `Player plays its last card and dwitches next waiting player and there is at least one other waiting player`() {
            val cardPlayed = Card.Clubs3
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertPlayerIsDoneForRound(p1Id)
                .assertPlayerHasNotFinishedWithJoker(p1Id)

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.TurnPassed)
            PlayerRobot(gameStateUpdated, p3Id)
                .assertPlayerState(DwitchPlayerStatus.Waiting)
                .assertPlayerIsDwitched()
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Playing)
        }

        @Test
        fun `Player plays its last card and ends round`() {
            val cardPlayed = Card.Clubs4
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertPlayerIsDoneForRound(p1Id)
                .assertPlayerIsDoneForRound(p2Id)
                .assertPlayerHasNotFinishedWithJoker(p1Id)
                .assertPlayerHasNotFinishedWithJoker(p2Id)
                .assertRoundIsOver()

            PlayerRobot(gameStateUpdated, p1Id)
                .assertPlayerState(DwitchPlayerStatus.Done)
                .assertRank(DwitchRank.President)

            PlayerRobot(gameStateUpdated, p2Id)
                .assertPlayerState(DwitchPlayerStatus.Done)
                .assertRank(DwitchRank.Asshole)
        }

        @Test
        fun `Player plays its last card which is a joker`() {
            val cardPlayed = Card.Clubs2
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
                .setCardsdOnTable(Card.Spades3)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated)
                .assertPlayerIsDoneForRound(p1Id)
                .assertPlayerHasFinishedWithJoker(p1Id)
                .assertTableIsEmpty() // Because card played is joker
                .assertRoundIsNotOver()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `Player pawdadwda`() {
            val cardPlayed = Card.Clubs6
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.TurnPassed, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.Done, DwitchRank.President, listOf(Card.HeartsJack))
                .addPlayerToGame(p5, DwitchPlayerStatus.TurnPassed, DwitchRank.Neutral, listOf(Card.Clubs4))
                .setCardsdOnTable(Card.Spades4)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated).assertTableIsEmpty()

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p5Id).assertPlayerState(DwitchPlayerStatus.Waiting)
        }

        @Test
        fun `Player plays its last card blalbla`() {
            val cardPlayed = Card.ClubsQueen
            initialGameState = gameStateBuilder
                .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
                .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
                .addPlayerToGame(p3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
                .addPlayerToGame(p4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
                .addPlayerToGame(p5, DwitchPlayerStatus.TurnPassed, DwitchRank.Neutral, listOf(Card.Clubs4))
                .setCardsdOnTable(Card.Spades4, Card.DiamondsJack)
                .build()

            launchPlayCardTest(cardPlayed)

            GameStateRobot(gameStateUpdated).assertCardsOnTable(Card.Spades4, Card.DiamondsJack, Card.ClubsQueen)

            PlayerRobot(gameStateUpdated, p1Id).assertPlayerState(DwitchPlayerStatus.Done)
            PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
            PlayerRobot(gameStateUpdated, p3Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p4Id).assertPlayerState(DwitchPlayerStatus.Waiting)
            PlayerRobot(gameStateUpdated, p5Id).assertPlayerState(DwitchPlayerStatus.TurnPassed)
        }
    }

    @Test
    fun `Player plays after first Jack played of the round and breaks special rule by not passing`() {
        val cardPlayed = Card.ClubsAce
        initialGameState = gameStateBuilder
            .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.ClubsJack)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated).assertPlayerHasBrokenFirstJackPlayedRule(p1Id)

        PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `Player plays after second Jack played of the round and so does not break special rule`() {
        val cardPlayed = Card.ClubsAce
        initialGameState = gameStateBuilder
            .addPlayerToGame(p1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(p2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.ClubsJack)
            .setGraveyard(Card.HeartsJack, Card.DiamondsKing)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated).assertPlayerHasNotBrokenFirstJackPlayedRule(p1Id)

        PlayerRobot(gameStateUpdated, p2Id).assertPlayerState(DwitchPlayerStatus.Playing)
    }

    private fun launchPlayCardTest(cardPlayed: Card) {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(gameState).playCard(cardPlayed)
    }
}
