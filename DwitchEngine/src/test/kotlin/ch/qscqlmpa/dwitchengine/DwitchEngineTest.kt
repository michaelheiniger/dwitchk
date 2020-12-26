package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class DwitchEngineTest {

    private lateinit var hostPlayer: PlayerOnboardingInfo
    private lateinit var guestPlayer1: PlayerOnboardingInfo
    private lateinit var guestPlayer2: PlayerOnboardingInfo
    private lateinit var guestPlayer3: PlayerOnboardingInfo
    private lateinit var guestPlayer4: PlayerOnboardingInfo

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

        val initialGameSetup = DeterministicInitialGameSetup(
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

        // Playing order is host, guest1, guest2, guest3, guest4
        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.Clubs5))
            .assertGamePhase(GamePhase.RoundIsBeginning)
            .assertPlayingOrder(hostPlayerId, guestPlayer1Id, guestPlayer2Id, guestPlayer3Id, guestPlayer4Id)
        createPlayerInfoRobot(hostPlayerId)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
                .assertCanStartNewRound(false)
                .assertCanEndGame(false)

        // Host plays a card and dwitches guest1
        playCard(Card.Diamonds5)

        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.Clubs5, Card.Diamonds5))
            .assertGamePhase(GamePhase.RoundIsOnGoing)
        createPlayerInfoRobot(guestPlayer1Id).assertDwitched()
        createPlayerInfoRobot(guestPlayer2Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Guest2 plays a joker
        playCard(Card.Hearts2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(guestPlayer2Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Guest2 plays a card
        playCard(Card.Hearts3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3))
        createPlayerInfoRobot(guestPlayer3Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Guest3 plays a card
        playCard(Card.Spades3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3))
        createPlayerInfoRobot(hostPlayerId)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Host plays a card
        playCard(Card.Diamonds3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3, Card.Diamonds3))
        createPlayerInfoRobot(guestPlayer2Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Guest2 plays its last card
        playCard(Card.Hearts4)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3, Card.Diamonds3, Card.Hearts4))
        createPlayerInfoRobot(guestPlayer3Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)
        createPlayerInfoRobot(guestPlayer2Id).assertPlayerStatus(PlayerStatus.Done)

        // Guest3 plays a joker
        playCard(Card.Spades2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(guestPlayer3Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Guest3 plays its last card
        playCard(Card.Spades4)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Spades4))
        createPlayerInfoRobot(guestPlayer3Id).assertPlayerStatus(PlayerStatus.Done)
        createPlayerInfoRobot(guestPlayer4Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Guest4 plays a card
        playCard(Card.DiamondsJack)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Spades4, Card.DiamondsJack))
        createPlayerInfoRobot(hostPlayerId)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Host plays its last card
        playCard(Card.Diamonds2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(hostPlayerId).assertPlayerStatus(PlayerStatus.Done)
        createPlayerInfoRobot(guestPlayer1Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Play a card
        playCard(Card.Clubs3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs3))
        createPlayerInfoRobot(guestPlayer4Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Play a card
        playCard(Card.ClubsQueen)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs3, Card.ClubsQueen))
        createPlayerInfoRobot(guestPlayer1Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Play a card
        playCard(Card.Clubs2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(guestPlayer1Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Play a card
        playCard(Card.Clubs6)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs6))
        createPlayerInfoRobot(guestPlayer4Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Play a card
        playCard(Card.HeartsKing)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs6, Card.HeartsKing))
        createPlayerInfoRobot(guestPlayer1Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Pick a card
        pickCard()

        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.Clubs6, Card.HeartsKing))
            .assertGameEvent(null)
        createPlayerInfoRobot(guestPlayer1Id)
                .assertCanPlay(true)
                .assertCanPass(true)
                .assertCanPickACard(false)

        // Pass turn
        passTurn()

        createGameInfoRobot()
            .assertCardsOnTable(emptyList())
            .assertGameEvent(GameEvent.TableHasBeenClearedTurnPassed)
            .assertGamePhase(GamePhase.RoundIsOnGoing)
        createPlayerInfoRobot(guestPlayer4Id)
                .assertCanPlay(true)
                .assertCanPass(false)
                .assertCanPickACard(true)

        // Play a card
        playCard(Card.SpadesAce)

        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.SpadesAce))
            .assertGameEvent(null)
            .assertGamePhase(GamePhase.RoundIsOver)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerStatus(PlayerStatus.Done)
            .assertPlayerRank(Rank.ViceAsshole)
        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerRank(Rank.President)
        createPlayerInfoRobot(guestPlayer3Id)
            .assertPlayerRank(Rank.VicePresident)
        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerRank(Rank.Asshole) // Finished with a Joker

        createPlayerInfoRobot(guestPlayer4Id)
                .assertCanPlay(false)
                .assertCanPass(false)
                .assertCanPickACard(false)
                .assertPlayerStatus(PlayerStatus.Done)
                .assertPlayerRank(Rank.Neutral)
                .assertCanStartNewRound(true)
                .assertCanEndGame(true)
    }

    private fun playCard(card: Card) {
        gameState = DwitchEngineImpl(gameState).playCard(card)
    }

    private fun pickCard() {
        gameState = DwitchEngineImpl(gameState).pickCard()
    }

    private fun passTurn() {
        gameState = DwitchEngineImpl(gameState).passTurn()
    }

    private fun createPlayerInfoRobot(playerId: PlayerInGameId): PlayerInfoRobot {
        return PlayerInfoRobot(DwitchEngineImpl(gameState).getGameInfo().playerInfos.getValue(playerId))
    }

    private fun createGameInfoRobot(): GameInfoRobot {
        return GameInfoRobot(DwitchEngineImpl(gameState).getGameInfo())
    }
}