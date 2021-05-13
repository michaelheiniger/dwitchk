package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.DwitchEngineImpl
import ch.qscqlmpa.dwitchengine.EngineTestBase
import ch.qscqlmpa.dwitchengine.GameStateRobot
import ch.qscqlmpa.dwitchengine.PlayerRobot
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartNewRoundTest : EngineTestBase() {

    private lateinit var cardDealerFactory: DeterministicCardDealerFactory

    @BeforeEach
    override fun setup() {
        super.setup()
        gameStateBuilder
            .setGamePhase(DwitchGamePhase.RoundIsOver)
            .setCurrentPlayer(p2Id)
    }

    @Test
    fun `Start a new round for 2 players`() {
        initialGameState = gameStateBuilder
            .addPlayerToGame(p1, DwitchPlayerStatus.Done, DwitchRank.Asshole, emptyList())
            .addPlayerToGame(p2, DwitchPlayerStatus.Done, DwitchRank.President, emptyList())
            .build()

        val playersCards = mapOf(
            p1Id to setOf(Card.Clubs2, Card.Clubs3, Card.Clubs4),
            p2Id to setOf(Card.Clubs5, Card.Clubs6, Card.Clubs7)
        )
        val numCardsDealt = playersCards.values.map { p -> p.size }.sum()

        setupCardDealer(playersCards)

        launchStartNewRoundTest()
        GameStateRobot(gameStateUpdated)
            .assertGamePhase(DwitchGamePhase.CardExchange)
            .assertTableIsEmpty()
            .assertNumCardsInDeck(CardUtil.deckSize - numCardsDealt)
            .assertPlayingOrder(listOf(p1Id, p2Id))
            .assertActivePlayers(p1Id, p2Id)
            .assertCurrentPlayerId(p1Id)

        PlayerRobot(gameStateUpdated, p1Id)
            .assertCardsInHandContainsExactly(Card.Clubs2, Card.Clubs3, Card.Clubs4)
            .assertPlayerState(DwitchPlayerStatus.Playing) // Since Asshole
            .assertPlayerIsNotDwitched()

        PlayerRobot(gameStateUpdated, p2Id)
            .assertCardsInHandContainsExactly(Card.Clubs5, Card.Clubs6, Card.Clubs7)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsNotDwitched()
    }

    @Test
    fun `Start a new round for 5 players`() {
        initialGameState = gameStateBuilder
            .addPlayerToGame(p1, DwitchPlayerStatus.Done, DwitchRank.Neutral, emptyList())
            .addPlayerToGame(p2, DwitchPlayerStatus.Done, DwitchRank.President, emptyList())
            .addPlayerToGame(p3, DwitchPlayerStatus.Done, DwitchRank.VicePresident, emptyList())
            .addPlayerToGame(p4, DwitchPlayerStatus.Done, DwitchRank.ViceAsshole, emptyList())
            .addPlayerToGame(p5, DwitchPlayerStatus.Done, DwitchRank.Asshole, emptyList())
            .build()

        val playersCards = mapOf(
            p1Id to setOf(Card.Clubs5, Card.Hearts5, Card.Spades5),
            p2Id to setOf(Card.Diamonds2, Card.Diamonds3, Card.Diamonds4),
            p3Id to setOf(Card.Clubs2, Card.Clubs3, Card.Clubs4),
            p4Id to setOf(Card.Spades2, Card.Spades3, Card.Spades4),
            p5Id to setOf(Card.Hearts2, Card.Hearts3, Card.Hearts4),
        )
        val numCardsDealt = playersCards.values.map { p -> p.size }.sum()

        setupCardDealer(playersCards)

        launchStartNewRoundTest()
        GameStateRobot(gameStateUpdated)
            .assertGamePhase(DwitchGamePhase.CardExchange)
            .assertTableIsEmpty()
            .assertNumCardsInDeck(CardUtil.deckSize - numCardsDealt)
            .assertPlayingOrder(listOf(p5Id, p4Id, p1Id, p3Id, p2Id))
            .assertActivePlayers(p1Id, p2Id, p3Id, p4Id, p5Id)
            .assertCurrentPlayerId(p5Id)

        PlayerRobot(gameStateUpdated, p1Id)
            .assertCardsInHandContainsExactly(Card.Clubs5, Card.Hearts5, Card.Spades5)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsNotDwitched()

        PlayerRobot(gameStateUpdated, p2Id)
            .assertCardsInHandContainsExactly(Card.Diamonds2, Card.Diamonds3, Card.Diamonds4)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsNotDwitched()

        PlayerRobot(gameStateUpdated, p3Id)
            .assertCardsInHandContainsExactly(Card.Clubs2, Card.Clubs3, Card.Clubs4)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsNotDwitched()

        PlayerRobot(gameStateUpdated, p4Id)
            .assertCardsInHandContainsExactly(Card.Spades2, Card.Spades3, Card.Spades4)
            .assertPlayerState(DwitchPlayerStatus.Waiting)
            .assertPlayerIsNotDwitched()

        PlayerRobot(gameStateUpdated, p5Id)
            .assertCardsInHandContainsExactly(Card.Hearts2, Card.Hearts3, Card.Hearts4)
            .assertPlayerState(DwitchPlayerStatus.Playing) // Since Asshole
            .assertPlayerIsNotDwitched()
    }

    @Test
    fun `Game is properly updated for new round`() {
        initialGameState = gameStateBuilder
            .addPlayerToGame(p1, DwitchPlayerStatus.Done, DwitchRank.Asshole, emptyList())
            .addPlayerToGame(p2, DwitchPlayerStatus.Done, DwitchRank.President, emptyList())

            // These two statements are mutually exclusive but the goal is to check the reset of their values.
            .setGameEvent(DwitchGameEvent.TableHasBeenCleared(PlayedCards(Card.Hearts2)))
            .setCardsdOnTable(PlayedCards(Card.Hearts5), PlayedCards(Card.Diamonds7), PlayedCards(Card.Hearts2))
            .setJoker(CardName.Ace)
            .build()

        val playersCards = mapOf(
            p1Id to setOf(Card.Clubs2, Card.Clubs3, Card.Clubs4),
            p2Id to setOf(Card.Clubs5, Card.Clubs6, Card.Clubs7)
        )

        setupCardDealer(playersCards)

        launchStartNewRoundTest()
        GameStateRobot(gameStateUpdated)
            .assertGamePhase(DwitchGamePhase.CardExchange)
            .assertTableIsEmpty()
            .assertNumCardsInGraveyard(0)
            .assertPlayersDoneForRoundIsEmpty()
            .assertJoker(CardName.Two)
            .assertGameEvent(null)
    }

    private fun setupCardDealer(cardsForPlayer: Map<DwitchPlayerId, Set<Card>>) {
        cardDealerFactory = DeterministicCardDealerFactory(DeterministicCardDealer(cardsForPlayer))
    }

    private fun launchStartNewRoundTest() {
        val gameState = gameStateBuilder.build()
        gameStateUpdated = DwitchEngineImpl(gameState).startNewRound(cardDealerFactory)
    }
}
