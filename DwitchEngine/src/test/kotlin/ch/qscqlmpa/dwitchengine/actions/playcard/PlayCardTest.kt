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
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayCardTest : EngineTestBase() {

    @BeforeEach
    override fun setup() {
        super.setup()
        gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOnGoing)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
    }

    @Test
    fun `Player plays a valid move, no dwitch, no joker`() {
        val cardPlayed = Card.Clubs7
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Spades5))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Clubs3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertNumCardsOnTable(2)
            .assertCardsOnTableContains(Card.Clubs3, cardPlayed) // Card has been added to table

        PlayerRobot(gameStateUpdated, player1Id)
            .assertNumCardsInHand(1)
            .assertCardsInHandContains(Card.Spades5) // Card has been removed from hand
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `Player plays a valid move next player is dwitched`() {
        val cardPlayed = Card.Clubs4 // Same card as last card on the table
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Spades5))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades4)
            .build()

        assertThat(initialGameState.lastCardOnTable()!!.value()).isEqualTo(cardPlayed.value())

        launchPlayCardTest(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
            .assertPlayerIsNotDwitched()
    }

    @Test
    fun `Player plays its last card and is done`() {
        val cardPlayed = Card.Clubs4
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Player plays its last card which is a joker and is done`() {
        val cardPlayed = Card.Clubs2
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasFinishedWithJoker(player1Id)
            .assertTableIsCleared() // Because card played is joker
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Player plays its last card and is done, dwitches the next player`() {
        val cardPlayed = Card.Clubs3 // Same card as last card on the table
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
            .assertPlayerIsNotDwitched()
    }

    @Test
    fun `Player plays its last card and ends round`() {
        val cardPlayed = Card.Clubs4
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerIsDoneForRound(player2Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player2Id)
            .assertRoundIsOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Done)
            .assertRank(DwitchRank.President)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Done)
            .assertRank(DwitchRank.Asshole)
    }

    @Test
    fun `Player plays an invalid card, an error is triggered since this should not happen`() {
        val cardPlayed = Card.Clubs3 // Lower value than last card on table: invalid
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Spades4)
            .build()

        assertThrows(IllegalArgumentException::class.java) { launchPlayCardTest(cardPlayed) }
    }

    @Test
    fun `Player is done and next waiting player is dwitched and there is no other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertTableIsCleared()
            .assertGameEvent(DwitchGameEvent.TableHasBeenCleared(cardPlayed))
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Player is done and next waiting player is dwitched and there is at least one other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `Player is not done and next waiting player is dwitched and there is no other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Player is not done and next waiting player is dwitched and there is at least one other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableContains(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `Player is not done and next waiting player is not dwitched`() {
        val cardPlayed = Card.Clubs4
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableContains(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Player plays joker and is not done`() {
        val cardPlayed = Card.Clubs2
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, DwitchPlayerStatus.TurnPassed, DwitchRank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, DwitchPlayerStatus.Waiting, DwitchRank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, DwitchPlayerStatus.TurnPassed, DwitchRank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades4)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Player plays after first Jack played of the round and breaks special rule by not passing`() {
        val cardPlayed = Card.ClubsAce
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.ClubsJack)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerHasBrokenFirstJackPlayedRule(player1Id)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
    }

    @Test
    fun `Player plays after second Jack played of the round and so does not break special rule`() {
        val cardPlayed = Card.ClubsAce
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, DwitchPlayerStatus.Playing, DwitchRank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, DwitchPlayerStatus.Waiting, DwitchRank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.ClubsJack)
            .setGraveyard(Card.HeartsJack, Card.DiamondsKing)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerHasNotBrokenFirstJackPlayedRule(player1Id)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(DwitchPlayerStatus.Playing)
    }

    private fun launchPlayCardTest(cardPlayed: Card) {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(gameState).playCard(cardPlayed)
    }
}
