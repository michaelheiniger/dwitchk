package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayCardTest : EngineTestBase() {

    @BeforeEach
    override fun setup() {
        super.setup()
        gameStateBuilder
            .setGamePhase(GamePhase.RoundIsOnGoing)
            .setLocalPlayer(player1Id)
            .setCurrentPlayer(player1Id)
    }

    @Test
    fun `Player plays a valid move, no dwitch, no joker`() {
        val cardPlayed = Card.Clubs7
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Spades5))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Clubs3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertNumCardsOnTable(2)
            .assertCardsOnTableContains(Card.Clubs3, cardPlayed) // Card has been added to table

        PlayerRobot(gameStateUpdated, player1Id)
            .assertNumCardsInHand(1)
            .assertCardsInHandContains(Card.Spades5) // Card has been removed from hand
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)
    }

    @Test
    fun `Player plays a valid move next player is dwitched`() {
        val cardPlayed = Card.Clubs4 // Same card as last card on the table
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Spades5))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades4)
            .build()

        assertThat(initialGameState.lastCardOnTable()!!.value()).isEqualTo(cardPlayed.value())

        launchPlayCardTest(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Playing)
            .assertPlayerIsNotDwitched()
    }

    @Test
    fun `Player plays its last card and becomes "done"`() {
        val cardPlayed = Card.Clubs4
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player plays its last card which is a joker and is done`() {
        val cardPlayed = Card.Clubs2
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasFinishedWithJoker(player1Id)
            .assertTableIsCleared() // Because card played is joker
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player plays its last card and is done, dwitches the next player`() {
        val cardPlayed = Card.Clubs3 // Same card as last card on the table
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.Neutral, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.President, listOf(Card.Spades10))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Playing)
            .assertPlayerIsNotDwitched()
    }

    @Test
    fun `Player plays its last card and ends round`() {
        val cardPlayed = Card.Clubs4
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Diamonds4))
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
            .assertPlayerState(PlayerStatus.Done)
            .assertRank(Rank.President)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Done)
            .assertRank(Rank.Asshole)
    }

    @Test
    fun `Player plays an invalid card, an error is triggered since this should not happen`() {
        val cardPlayed = Card.Clubs3 // Lower value than last card on table: invalid
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Spades4)
            .build()

        assertThrows(IllegalArgumentException::class.java) { launchPlayCardTest(cardPlayed) }
    }

    @Test
    fun `Player is done and next waiting player is dwitched and there is no other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, PlayerStatus.TurnPassed, Rank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)
            .assertTableIsCleared()
            .assertGameEvent(GameEvent.TableHasBeenCleared(cardPlayed))
            .assertRoundIsNotOver()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player is done and next waiting player is dwitched and there is at least one other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerIsDoneForRound(player1Id)
            .assertPlayerHasNotFinishedWithJoker(player1Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Done)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Playing)
    }

    @Test
    fun `Player is not done and next waiting player is dwitched and there is no other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, PlayerStatus.TurnPassed, Rank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player is not done and next waiting player is dwitched and there is at least one other waiting player`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableContains(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)
            .assertPlayerIsDwitched()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Playing)
    }

    @Test
    fun `Player is not done and next waiting player is not dwitched`() {
        val cardPlayed = Card.Clubs4
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, PlayerStatus.Waiting, Rank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableContains(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.TurnPassed)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player plays joker and is not done`() {
        val cardPlayed = Card.Clubs2
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .addPlayerToGame(player3, PlayerStatus.Waiting, Rank.VicePresident, listOf(Card.Hearts10))
            .addPlayerToGame(player4, PlayerStatus.TurnPassed, Rank.President, listOf(Card.HeartsJack))
            .setCardsdOnTable(Card.Spades4)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertTableIsCleared()

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Playing)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player3Id)
            .assertPlayerState(PlayerStatus.Waiting)

        PlayerRobot(gameStateUpdated, player4Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player has picked a card and can still play afterwards hence cannot pick another card`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(
                player1,
                PlayerStatus.Playing,
                Rank.Asshole,
                listOf(cardPlayed, Card.Diamonds5),
                hasPickedCard = true
            )
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Playing)
            .assertPlayerHasPickedCard() // Has not been reset since player1 can still play

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player has picked a card and cannot play another card so it will be able to pick a card next time it can play`() {
        val cardPlayed = Card.Clubs3
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.TurnPassed, Rank.ViceAsshole, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.Spades3)
            .build()

        launchPlayCardTest(cardPlayed)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertPlayerState(PlayerStatus.Playing)
            .assertPlayerHasNotPickedCard() // Has been reset since player1 is no longer Playing

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Waiting)
    }

    @Test
    fun `Player plays after first Jack played of the round and breaks special rule by not picking a card and then passing`() {
        val cardPlayed = Card.ClubsAce
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.ClubsJack)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerHasBrokenFirstJackPlayedRule(player1Id)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)
    }

    @Test
    fun `Player plays after second Jack played of the round and so does not break special rule`() {
        val cardPlayed = Card.ClubsAce
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerStatus.Playing, Rank.Asshole, listOf(cardPlayed, Card.Diamonds5))
            .addPlayerToGame(player2, PlayerStatus.Waiting, Rank.President, listOf(Card.Diamonds4))
            .setCardsdOnTable(Card.ClubsJack)
            .setGraveyard(Card.HeartsJack, Card.DiamondsKing)
            .build()

        launchPlayCardTest(cardPlayed)

        GameStateRobot(gameStateUpdated)
            .assertPlayerHasNotBrokenFirstJackPlayedRule(player1Id)

        PlayerRobot(gameStateUpdated, player2Id)
            .assertPlayerState(PlayerStatus.Playing)
    }

    private fun launchPlayCardTest(cardPlayed: Card) {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(gameState).playCard(cardPlayed)
    }
}
