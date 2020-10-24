package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartNewRoundTest : EngineTestBase() {

    private lateinit var cardDealerFactory: DeterministicCardDealerFactory

    @BeforeEach
    override fun setup() {
        super.setup()
        gameStateBuilder
            .setGamePhase(GamePhase.RoundIsOver)
            .setLocalPlayer(player2Id)
            .setCurrentPlayer(player2Id)
    }

    @Test
    fun `Start a new round for 2 players`() {
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerState.Done, Rank.Asshole, emptyList())
            .addPlayerToGame(player2, PlayerState.Done, Rank.President, emptyList())
            .build()

        setupCardDealer(
            mapOf(
                0 to listOf(Card.Clubs2, Card.Clubs3, Card.Clubs4),
                1 to listOf(Card.Clubs5, Card.Clubs6, Card.Clubs7)
            )
        )

        launchStartNewRoundTest()
        GameStateRobot(gameStateUpdated)
            .assertCardsOnTableContainsExactly(Card.Clubs8)
            .assertNumCardsInDeck(CardUtil.deckSize - 7)
            .assertPlayingOrder(listOf(player1Id, player2Id))
            .assertActivePlayers(player1Id, player2Id)
            .assertCurrentPlayerId(player1Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertCardsInHandContainsExactly(Card.Clubs2, Card.Clubs3, Card.Clubs4)
            .assertPlayerState(PlayerState.Playing) // Since Asshole
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()

        PlayerRobot(gameStateUpdated, player2Id)
            .assertCardsInHandContainsExactly(Card.Clubs5, Card.Clubs6, Card.Clubs7)
            .assertPlayerState(PlayerState.Waiting)
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()
    }

    @Test
    fun `Start a new round for 5 players`() {
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerState.Done, Rank.Neutral, emptyList())
            .addPlayerToGame(player2, PlayerState.Done, Rank.President, emptyList())
            .addPlayerToGame(player3, PlayerState.Done, Rank.VicePresident, emptyList())
            .addPlayerToGame(player4, PlayerState.Done, Rank.ViceAsshole, emptyList())
            .addPlayerToGame(player5, PlayerState.Done, Rank.Asshole, emptyList())
            .build()

        setupCardDealer(
            mapOf(
                0 to listOf(Card.Clubs5, Card.Hearts5, Card.Spades5),
                1 to listOf(Card.Diamonds2, Card.Diamonds3, Card.Diamonds4),
                2 to listOf(Card.Clubs2, Card.Clubs3, Card.Clubs4),
                3 to listOf(Card.Spades2, Card.Spades3, Card.Spades4),
                4 to listOf(Card.Hearts2, Card.Hearts3, Card.Hearts4),
            )
        )

        launchStartNewRoundTest()
        GameStateRobot(gameStateUpdated)
            .assertCardsOnTableContainsExactly(Card.Clubs6)
            .assertNumCardsInDeck(CardUtil.deckSize - 16)
            .assertPlayingOrder(listOf(player5Id, player4Id, player1Id, player3Id, player2Id))
            .assertActivePlayers(player1Id, player2Id, player3Id, player4Id, player5Id)
            .assertCurrentPlayerId(player5Id)

        PlayerRobot(gameStateUpdated, player1Id)
            .assertCardsInHandContainsExactly(Card.Clubs2, Card.Clubs3, Card.Clubs4)
            .assertPlayerState(PlayerState.Waiting)
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()

        PlayerRobot(gameStateUpdated, player2Id)
            .assertCardsInHandContainsExactly(Card.Hearts2, Card.Hearts3, Card.Hearts4)
            .assertPlayerState(PlayerState.Waiting)
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()

        PlayerRobot(gameStateUpdated, player3Id)
            .assertCardsInHandContainsExactly(Card.Spades2, Card.Spades3, Card.Spades4)
            .assertPlayerState(PlayerState.Waiting)
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()

        PlayerRobot(gameStateUpdated, player4Id)
            .assertCardsInHandContainsExactly(Card.Diamonds2, Card.Diamonds3, Card.Diamonds4)
            .assertPlayerState(PlayerState.Waiting)
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()

        PlayerRobot(gameStateUpdated, player5Id)
            .assertCardsInHandContainsExactly(Card.Clubs5, Card.Hearts5, Card.Spades5)
            .assertPlayerState(PlayerState.Playing) // Since Asshole
            .assertPlayerIsNotDwitched()
            .assertPlayerHasNotPickedCard()
    }

    @Test
    fun `Game is properly reset for new round`() {
        initialGameState = gameStateBuilder
            .addPlayerToGame(player1, PlayerState.Done, Rank.Asshole, emptyList())
            .addPlayerToGame(player2, PlayerState.Done, Rank.President, emptyList())

            // These two statements are mutually exclusive but the goal is to check the reset of their values.
            .setGameEvent(GameEvent.TableHasBeenCleared(Card.Hearts2))
            .setCardsdOnTable(Card.Hearts5, Card.Diamonds7, Card.Hearts2)

            .setJoker(CardName.Ace)
            .build()

        setupCardDealer(
            mapOf(
                0 to listOf(Card.Clubs2, Card.Clubs3, Card.Clubs4),
                1 to listOf(Card.Clubs5, Card.Clubs6, Card.Clubs7)
            )
        )

        launchStartNewRoundTest()
        GameStateRobot(gameStateUpdated)
            .assertCardsOnTableContainsExactly(Card.Clubs8)
            .assertNumCardsInGraveyard(0)
            .assertPlayersDoneForRoundIsEmpty()
            .assertJoker(CardName.Two)
            .assertGameEvent(null)
            .assertGamePhase(GamePhase.RoundIsBeginning)
    }

    private fun setupCardDealer(cardsForPlayer: Map<Int, List<Card>>) {
        cardDealerFactory = DeterministicCardDealerFactory(DeterministicCardDealer(cardsForPlayer))

    }

    private fun launchStartNewRoundTest() {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngine(gameState).startNewRound(cardDealerFactory)
    }
}