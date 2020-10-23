package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class DwitchEngineTest {

    private lateinit var hostPlayer: PlayerInfo
    private lateinit var guestPlayer1: PlayerInfo
    private lateinit var guestPlayer2: PlayerInfo
    private lateinit var guestPlayer3: PlayerInfo
    private lateinit var guestPlayer4: PlayerInfo

    private lateinit var hostPlayerId: PlayerInGameId
    private lateinit var guestPlayer1Id: PlayerInGameId
    private lateinit var guestPlayer2Id: PlayerInGameId
    private lateinit var guestPlayer3Id: PlayerInGameId
    private lateinit var guestPlayer4Id: PlayerInGameId

    private lateinit var gameState: GameState

    @BeforeEach
    fun setup() {
        hostPlayer = TestEntityFactory.createHostPlayerInfo()
        guestPlayer1 = TestEntityFactory.createGuestPlayer1Info()
        guestPlayer2 = TestEntityFactory.createGuestPlayer2Info()
        guestPlayer3 = TestEntityFactory.createGuestPlayer3Info()
        guestPlayer4 = TestEntityFactory.createGuestPlayer4Info()

        hostPlayerId = hostPlayer.id
        guestPlayer1Id = guestPlayer1.id
        guestPlayer2Id = guestPlayer2.id
        guestPlayer3Id = guestPlayer3.id
        guestPlayer4Id = guestPlayer4.id

        val hostCards = listOf(Card.Diamonds2, Card.Diamonds3, Card.Diamonds5)
        val guest1Cards = listOf(Card.Clubs2, Card.Clubs3, Card.Clubs4, Card.Clubs6)
        val guest2Cards = listOf(Card.Hearts2, Card.Hearts3, Card.Hearts4)
        val guest3Cards = listOf(Card.Spades2, Card.Spades3, Card.Spades4)
        val guest4Cards = listOf(Card.DiamondsJack, Card.ClubsQueen, Card.HeartsKing, Card.SpadesAce)

        val initialGameSetup = DeterministicInitialGameSetup(numPlayers = 5)
        initialGameSetup.initialize(
                mapOf(
                        0 to hostCards,
                        1 to guest1Cards,
                        2 to guest2Cards,
                        3 to guest3Cards,
                        4 to guest4Cards
                ),
                mapOf(
                        0 to Rank.Asshole,
                        1 to Rank.ViceAsshole,
                        2 to Rank.Neutral,
                        3 to Rank.VicePresident,
                        4 to Rank.President
                )
        )

        gameState = GameBootstrap.createNewGame(
                listOf(hostPlayer, guestPlayer1, guestPlayer2, guestPlayer3, guestPlayer4),
                initialGameSetup
        )
    }

    /**
     * Simulate a whole round.
     * Orders in which the players finish:
     * - Guest2 -> President
     * - Guest3 -> Vice-President
     * - Host -> Asshole since it plays "joker" as last card (special rule)
     * - Guest4 -> Neutral
     * - Guest1 -> Vice-Asshole
     */
    @Test
    fun `Play whole round with 5 players`() {
        PlayerDashboardRobot(getPlayerDashboard(hostPlayerId))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Clubs5))
                .assertCanStartNewRound(false)
                .assertCanEndGame(false)
        playCard(Card.Diamonds5)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer2Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Clubs5, Card.Diamonds5))
        playCard(Card.Hearts2)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer2Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(emptyList())
        playCard(Card.Hearts3)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer3Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Hearts3))
        playCard(Card.Spades3)

        PlayerDashboardRobot(getPlayerDashboard(hostPlayerId))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3))
        playCard(Card.Diamonds3)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer2Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3, Card.Diamonds3))
        playCard(Card.Hearts4)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer3Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3, Card.Diamonds3, Card.Hearts4))
                .assertPlayerState(guestPlayer2Id, PlayerState.Done)
        playCard(Card.Spades2)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer3Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(emptyList())
        playCard(Card.Spades4)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer4Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Spades4))
                .assertPlayerState(guestPlayer3Id, PlayerState.Done)
        playCard(Card.DiamondsJack)

        PlayerDashboardRobot(getPlayerDashboard(hostPlayerId))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Spades4, Card.DiamondsJack))
        playCard(Card.Diamonds2)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer1Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(emptyList())
                .assertPlayerState(hostPlayerId, PlayerState.Done)
        playCard(Card.Clubs3)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer4Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Clubs3))
        playCard(Card.ClubsQueen)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer1Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.ClubsQueen))
        playCard(Card.Clubs2)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer1Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(emptyList())
        playCard(Card.Clubs6)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer4Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Clubs6))
        playCard(Card.HeartsKing)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer1Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(listOf(Card.Clubs6, Card.HeartsKing))
        pickCard()

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer1Id))
                .assertCanPlay(true)
                .assertCanPass(true)
                .assertCanPickACard(false)
                .assertCardsOnTable(listOf(Card.Clubs6, Card.HeartsKing))
                .assertGameEvent(null)
        passTurn()

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer4Id))
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCardsOnTable(emptyList())
                .assertGameEvent(GameEvent.TableHasBeenClearedTurnPassed)
                .assertGamePhase(GamePhase.RoundIsOnGoing)
        playCard(Card.SpadesAce)

        PlayerDashboardRobot(getPlayerDashboard(guestPlayer4Id))
                .assertCanPlay(false)
                .assertCanPass(false)
                .assertCanPickACard(false)
                .assertCardsOnTable(listOf(Card.SpadesAce))
                .assertPlayerState(guestPlayer4Id, PlayerState.Done)
                .assertPlayerState(guestPlayer1Id, PlayerState.Done)
                .assertPlayerRank(guestPlayer2Id, Rank.President)
                .assertPlayerRank(guestPlayer3Id, Rank.VicePresident)
                .assertPlayerRank(guestPlayer1Id, Rank.ViceAsshole)
                .assertPlayerRank(hostPlayerId, Rank.Asshole) // Finished with a Joker
                .assertPlayerRank(guestPlayer4Id, Rank.Neutral)
                .assertGameEvent(null)
                .assertGamePhase(GamePhase.RoundIsOver)
                .assertCanStartNewRound(true)
                .assertCanEndGame(true)
    }

    private fun playCard(card: Card) {
        gameState = DwitchEngine(gameState).playCard(card)
    }

    private fun pickCard() {
        gameState = DwitchEngine(gameState).pickCard()
    }

    private fun passTurn() {
        gameState = DwitchEngine(gameState).passTurn()
    }

    private fun getPlayerDashboard(playerId: PlayerInGameId): PlayerDashboard {
        return DwitchEngine(gameState).getPlayerDashboard(playerId)
    }
}