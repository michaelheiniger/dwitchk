package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DwitchEngineTest {

    private lateinit var p1: DwitchPlayerOnboardingInfo
    private lateinit var p2: DwitchPlayerOnboardingInfo
    private lateinit var p3: DwitchPlayerOnboardingInfo
    private lateinit var p4: DwitchPlayerOnboardingInfo
    private lateinit var p5: DwitchPlayerOnboardingInfo

    private lateinit var p1Id: DwitchPlayerId
    private lateinit var p2Id: DwitchPlayerId
    private lateinit var p3Id: DwitchPlayerId
    private lateinit var p4Id: DwitchPlayerId
    private lateinit var p5Id: DwitchPlayerId

    private lateinit var gameState: DwitchGameState

    @BeforeEach
    fun setup() {
        p1 = TestEntityFactory.createHostPlayerInfo()
        p2 = TestEntityFactory.createGuestPlayer1Info()
        p3 = TestEntityFactory.createGuestPlayer2Info()
        p4 = TestEntityFactory.createGuestPlayer3Info()
        p5 = TestEntityFactory.createGuestPlayer4Info()

        p1Id = p1.id
        p2Id = p2.id
        p3Id = p3.id
        p4Id = p4.id
        p5Id = p5.id
    }

    /**
     * Simulate a whole round.
     * Orders in which the players finish:
     * 1) Player 2 -> Asshole because it played a card on top of the first Jack of the round (see [DwitchGameState.playersWhoBrokeASpecialRule])
     * 2) Player 5 -> President
     * 3) Player 1 -> Vice-President
     * 4) Player 4 -> Neutral
     * 5) Player 3 -> Vice-Asshole
     */
    @Test
    fun `Play whole round with 5 players`() {
        val p1 = setOf(Card.Diamonds2, Card.Diamonds3, Card.Diamonds5)
        val p2 = setOf(Card.Clubs4, Card.ClubsQueen, Card.ClubsKing)
        val p3 = setOf(Card.Hearts3, Card.Hearts4, Card.Hearts5)
        val p4 = setOf(Card.Spades2, Card.Spades4, Card.Spades6)
        val p5 = setOf(Card.Diamonds10, Card.DiamondsJack, Card.HeartsAce)

        val initialGameSetup = DeterministicInitialGameSetup(
            mapOf(
                p1Id to p1,
                p2Id to p2,
                p3Id to p3,
                p4Id to p4,
                p5Id to p5
            ),
            mapOf(
                p1Id to DwitchRank.Asshole,
                p2Id to DwitchRank.ViceAsshole,
                p3Id to DwitchRank.Neutral,
                p4Id to DwitchRank.VicePresident,
                p5Id to DwitchRank.President
            )
        )

        gameState = GameBootstrap.createNewGame(
            listOf(this.p1, this.p2, this.p3, this.p4, this.p5),
            initialGameSetup
        )

        // Playing order is Player1, Player2, Player3, Player4, Player5
        createGameInfoRobot()
            .assertTableIsEmpty()
            .assertGamePhase(DwitchGamePhase.RoundIsBeginning)
            .assertPlayingOrder(p1Id, p2Id, p3Id, p4Id, p5Id)
            .assertNewRoundCanBeStarted(false)
        createPlayerInfoRobot(p1Id).assertCanPlay(true)
        createPlayerInfoRobot(p2Id).assertCanPlay(false)
        createPlayerInfoRobot(p3Id).assertCanPlay(false)
        createPlayerInfoRobot(p4Id).assertCanPlay(false)
        createPlayerInfoRobot(p5Id).assertCanPlay(false)

        // Player1 plays a card
        playCards(Card.Diamonds3)
        createGameInfoRobot()
            .assertCardsOnTable(PlayedCards(Card.Diamonds3))
            .assertGamePhase(DwitchGamePhase.RoundIsOnGoing)
        createPlayerInfoRobot(p1Id)
            .assertCanPlay(false)
            .assertHand(Card.Diamonds2, Card.Diamonds5)
        createPlayerInfoRobot(p2Id).assertCanPlay(true)

        // Player2 plays a card
        playCards(Card.Clubs4)
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.Diamonds3), PlayedCards(Card.Clubs4))
        createPlayerInfoRobot(p2Id).assertCanPlay(false)
        createPlayerInfoRobot(p3Id).assertCanPlay(true)

        // Player3 plays a card and dwitches Player4
        playCards(Card.Hearts4)
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.Diamonds3), PlayedCards(Card.Clubs4), PlayedCards(Card.Hearts4))
        createPlayerInfoRobot(p3Id).assertCanPlay(false)
        createPlayerInfoRobot(p4Id)
            .assertCanPlay(false)
            .assertIsDwitched()
        createPlayerInfoRobot(p5Id)
            .assertCanPlay(true)
            .assertIsNotDwitched()

        // Player5 plays a card
        playCards(Card.Diamonds10)
        createGameInfoRobot().assertCardsOnTable(
            PlayedCards(Card.Diamonds3),
            PlayedCards(Card.Clubs4),
            PlayedCards(Card.Hearts4),
            PlayedCards(Card.Diamonds10)
        )
        createPlayerInfoRobot(p5Id).assertCanPlay(false)
        createPlayerInfoRobot(p1Id).assertCanPlay(true)

        // Player1 passes
        passTurn()
        createGameInfoRobot().assertCardsOnTable(
            PlayedCards(Card.Diamonds3),
            PlayedCards(Card.Clubs4),
            PlayedCards(Card.Hearts4),
            PlayedCards(Card.Diamonds10)
        )
        createPlayerInfoRobot(p1Id)
            .assertCanPlay(false)
            .assertHand(Card.Diamonds2, Card.Diamonds5)
        createPlayerInfoRobot(p2Id).assertCanPlay(true)

        // Player2 plays a card
        playCards(Card.ClubsKing)
        createGameInfoRobot().assertCardsOnTable(
            PlayedCards(Card.Diamonds3),
            PlayedCards(Card.Clubs4),
            PlayedCards(Card.Hearts4),
            PlayedCards(Card.Diamonds10),
            PlayedCards(Card.ClubsKing)
        )
        createPlayerInfoRobot(p2Id).assertCanPlay(false)
        createPlayerInfoRobot(p3Id).assertCanPlay(true)

        // Player3 passes
        passTurn()
        createGameInfoRobot().assertCardsOnTable(
            PlayedCards(Card.Diamonds3),
            PlayedCards(Card.Clubs4),
            PlayedCards(Card.Hearts4),
            PlayedCards(Card.Diamonds10),
            PlayedCards(Card.ClubsKing)
        )
        createPlayerInfoRobot(p3Id).assertCanPlay(false)
        createPlayerInfoRobot(p4Id).assertCanPlay(true)

        // Player4 plays a joker
        playCards(Card.Spades2)
        createGameInfoRobot().assertTableIsEmpty()
        createPlayerInfoRobot(p4Id).assertCanPlay(true) // Player4 can play another card after a joker
        createPlayerInfoRobot(p5Id).assertCanPlay(false)

        // Player4 plays a card
        playCards(Card.Spades4)
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.Spades4))
        createPlayerInfoRobot(p4Id).assertCanPlay(false)
        createPlayerInfoRobot(p5Id).assertCanPlay(true)

        // Player5 plays a card: First Jack of the round !
        playCards(Card.DiamondsJack)
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.Spades4), PlayedCards(Card.DiamondsJack))
        createPlayerInfoRobot(p5Id).assertCanPlay(false)
        createPlayerInfoRobot(p1Id).assertCanPlay(true)

        // Player1 passes
        passTurn()
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.Spades4), PlayedCards(Card.DiamondsJack))
        createPlayerInfoRobot(p1Id).assertCanPlay(false)
        createPlayerInfoRobot(p2Id).assertCanPlay(true)

        // Player2 plays its last card
        playCards(Card.ClubsQueen) // Card played on top of the first Jack of the round: Player2 breaks a special rule !
        createGameInfoRobot().assertCardsOnTable(
            PlayedCards(Card.Spades4),
            PlayedCards(Card.DiamondsJack),
            PlayedCards(Card.ClubsQueen)
        )
        createPlayerInfoRobot(p2Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertHandIsEmpty()
        createPlayerInfoRobot(p3Id).assertCanPlay(true)

        // Player3 passes
        passTurn()
        createGameInfoRobot().assertCardsOnTable(
            PlayedCards(Card.Spades4),
            PlayedCards(Card.DiamondsJack),
            PlayedCards(Card.ClubsQueen)
        )
        createPlayerInfoRobot(p3Id).assertCanPlay(false)
        createPlayerInfoRobot(p4Id).assertCanPlay(true)

        // Player4 passes
        passTurn()
        createGameInfoRobot().assertTableIsEmpty() // Player2 is done, all other players except Player5 have passed -> reset
        createPlayerInfoRobot(p4Id).assertCanPlay(false)
        createPlayerInfoRobot(p5Id).assertCanPlay(true)

        // Player5 plays its last card
        playCards(Card.HeartsAce)
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.HeartsAce))
        createPlayerInfoRobot(p5Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertHandIsEmpty()
        createPlayerInfoRobot(p1Id).assertCanPlay(true)

        // Player1 plays a joker
        playCards(Card.Diamonds2)
        createGameInfoRobot().assertTableIsEmpty()
        createPlayerInfoRobot(p1Id).assertCanPlay(true)
        createPlayerInfoRobot(p2Id).assertCanPlay(false)

        // Player1 plays its last card
        playCards(Card.Diamonds5)
        createGameInfoRobot().assertCardsOnTable(PlayedCards(Card.Diamonds5))
        createPlayerInfoRobot(p1Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertHandIsEmpty()
        createPlayerInfoRobot(p3Id).assertCanPlay(true)

        // Player3 passes
        passTurn()
        createGameInfoRobot()
            .assertTableIsEmpty()
            .assertNewRoundCanBeStarted(false)
        createPlayerInfoRobot(p3Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Waiting) // Player3 passed its turn but there are only two remaining active players
        createPlayerInfoRobot(p4Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)
            .assertCanPlay(true)

        // Player4 plays its last card and ends the round
        playCards(Card.Spades6)
        createGameInfoRobot()
            .assertGamePhase(DwitchGamePhase.RoundIsOver)
            .assertNewRoundCanBeStarted(true)
        createPlayerInfoRobot(p1Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.VicePresident)
        createPlayerInfoRobot(p2Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.Asshole)
        createPlayerInfoRobot(p3Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.ViceAsshole)
        createPlayerInfoRobot(p4Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.Neutral)
        createPlayerInfoRobot(p5Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.President)

        // Start new round
        startNewRound()
        createGameInfoRobot().assertGamePhase(DwitchGamePhase.CardExchange)
        createPlayerInfoRobot(p1Id).assertCanPlay(false)
        createPlayerInfoRobot(p2Id).assertCanPlay(false)
        createPlayerInfoRobot(p3Id).assertCanPlay(false)
        createPlayerInfoRobot(p4Id).assertCanPlay(false)
        createPlayerInfoRobot(p5Id).assertCanPlay(false)

        // Card exchange
        assertCardExchangeRequired(p1Id) // VicePresident
        assertCardExchangeRequired(p2Id) // Asshole
        assertCardExchangeRequired(p3Id) // ViceAsshole
        assertNoCardExchangeRequired(p4Id) // Neutral
        assertCardExchangeRequired(p5Id) // President

        chooseCardsForExchange(p1Id, Card.Spades6)
        chooseCardsForExchange(p2Id, Card.Spades2, Card.DiamondsAce)
        chooseCardsForExchange(p3Id, Card.ClubsKing)
        chooseCardsForExchange(p5Id, Card.ClubsAce, Card.HeartsQueen)

        createGameInfoRobot().assertGamePhase(DwitchGamePhase.RoundIsBeginning)
        createPlayerInfoRobot(p1Id).assertCanPlay(false)
        createPlayerInfoRobot(p2Id).assertCanPlay(true) // Asshole starts
        createPlayerInfoRobot(p3Id).assertCanPlay(false)
        createPlayerInfoRobot(p4Id).assertCanPlay(false)
        createPlayerInfoRobot(p5Id).assertCanPlay(false)
    }

    private fun playCards(vararg cards: Card) {
        gameState = DwitchEngineImpl(gameState).playCards(PlayedCards(listOf(*cards)))
    }

    private fun passTurn() {
        gameState = DwitchEngineImpl(gameState).passTurn()
    }

    private fun startNewRound() {
        val p1 = setOf(Card.Clubs2, Card.Spades6, Card.HeartsAce) // VicePresident
        val p2 = setOf(Card.Spades2, Card.Diamonds3, Card.DiamondsAce) // Asshole
        val p3 = setOf(Card.Diamonds5, Card.HeartsJack, Card.ClubsKing) // ViceAsshole
        val p4 = setOf(Card.Spades5, Card.SpadesJack, Card.SpadesKing) // Neutral
        val p5 = setOf(Card.Clubs5, Card.ClubsAce, Card.HeartsQueen) // President

        val cardDealer = DeterministicCardDealer(mapOf(p1Id to p1, p2Id to p2, p3Id to p3, p4Id to p4, p5Id to p5))
        gameState = DwitchEngineImpl(gameState).startNewRound(DeterministicCardDealerFactory().setInstance(cardDealer))
    }

    private fun assertCardExchangeRequired(playerId: DwitchPlayerId) {
        assertThat(DwitchEngineImpl(gameState).getCardExchangeIfRequired(playerId)).isNotNull
    }

    private fun assertNoCardExchangeRequired(playerId: DwitchPlayerId) {
        assertThat(DwitchEngineImpl(gameState).getCardExchangeIfRequired(playerId)).isNull()
    }

    private fun chooseCardsForExchange(playerId: DwitchPlayerId, vararg cards: Card) {
        gameState = DwitchEngineImpl(gameState).chooseCardsForExchange(playerId, setOf(*cards))
    }

    private fun createPlayerInfoRobot(playerId: DwitchPlayerId): PlayerInfoRobot {
        return PlayerInfoRobot(DwitchEngineImpl(gameState).getGameInfo().playerInfos.getValue(playerId))
    }

    private fun createGameInfoRobot(): GameInfoRobot {
        return GameInfoRobot(DwitchEngineImpl(gameState).getGameInfo())
    }
}
