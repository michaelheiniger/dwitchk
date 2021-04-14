package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DwitchEngineTest {

    private lateinit var hostPlayer: DwitchPlayerOnboardingInfo
    private lateinit var guestPlayer1: DwitchPlayerOnboardingInfo
    private lateinit var guestPlayer2: DwitchPlayerOnboardingInfo
    private lateinit var guestPlayer3: DwitchPlayerOnboardingInfo
    private lateinit var guestPlayer4: DwitchPlayerOnboardingInfo

    private lateinit var hostPlayerId: DwitchPlayerId
    private lateinit var guestPlayer1Id: DwitchPlayerId
    private lateinit var guestPlayer2Id: DwitchPlayerId
    private lateinit var guestPlayer3Id: DwitchPlayerId
    private lateinit var guestPlayer4Id: DwitchPlayerId

    private lateinit var gameState: DwitchGameState

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
                0 to DwitchRank.Asshole,
                1 to DwitchRank.ViceAsshole,
                2 to DwitchRank.Neutral,
                3 to DwitchRank.VicePresident,
                4 to DwitchRank.President
            )
        )

        gameState = GameBootstrap.createNewGame(
            listOf(hostPlayer, guestPlayer1, guestPlayer2, guestPlayer3, guestPlayer4),
            initialGameSetup
        )

        // Playing order is host, guest1, guest2, guest3, guest4
        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.Clubs5))
            .assertGamePhase(DwitchGamePhase.RoundIsBeginning)
            .assertPlayingOrder(hostPlayerId, guestPlayer1Id, guestPlayer2Id, guestPlayer3Id, guestPlayer4Id)
        createPlayerInfoRobot(hostPlayerId)
            .assertCanPlay(true)
            .assertCanStartNewRound(false)

        // Host plays a card and dwitches guest1
        playCard(Card.Diamonds5)

        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.Clubs5, Card.Diamonds5))
            .assertGamePhase(DwitchGamePhase.RoundIsOnGoing)
        createPlayerInfoRobot(guestPlayer1Id).assertDwitched()
        createPlayerInfoRobot(guestPlayer2Id)
            .assertCanPlay(true)

        // Guest2 plays a joker
        playCard(Card.Hearts2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(guestPlayer2Id)
            .assertCanPlay(true)

        // Guest2 plays a card
        playCard(Card.Hearts3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3))
        createPlayerInfoRobot(guestPlayer3Id)
            .assertCanPlay(true)

        // Guest3 plays a card
        playCard(Card.Spades3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3))
        createPlayerInfoRobot(hostPlayerId)
            .assertCanPlay(true)

        // Host plays a card
        playCard(Card.Diamonds3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3, Card.Diamonds3))
        createPlayerInfoRobot(guestPlayer2Id)
            .assertCanPlay(true)

        // Guest2 plays its last card
        playCard(Card.Hearts4)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Hearts3, Card.Spades3, Card.Diamonds3, Card.Hearts4))
        createPlayerInfoRobot(guestPlayer3Id)
            .assertCanPlay(true)
        createPlayerInfoRobot(guestPlayer2Id).assertPlayerStatus(DwitchPlayerStatus.Done)

        // Guest3 plays a joker
        playCard(Card.Spades2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(guestPlayer3Id)
            .assertCanPlay(true)

        // Guest3 plays its last card
        playCard(Card.Spades4)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Spades4))
        createPlayerInfoRobot(guestPlayer3Id).assertPlayerStatus(DwitchPlayerStatus.Done)
        createPlayerInfoRobot(guestPlayer4Id)
            .assertCanPlay(true)

        // Guest4 plays a card
        playCard(Card.DiamondsJack)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Spades4, Card.DiamondsJack))
        createPlayerInfoRobot(hostPlayerId)
            .assertCanPlay(true)

        // Host plays its last card
        playCard(Card.Diamonds2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(hostPlayerId).assertPlayerStatus(DwitchPlayerStatus.Done)
        createPlayerInfoRobot(guestPlayer1Id)
            .assertCanPlay(true)

        // Play a card
        playCard(Card.Clubs3)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs3))
        createPlayerInfoRobot(guestPlayer4Id)
            .assertCanPlay(true)

        // Play a card
        playCard(Card.ClubsQueen)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs3, Card.ClubsQueen))
        createPlayerInfoRobot(guestPlayer1Id)
            .assertCanPlay(true)

        // Play a card
        playCard(Card.Clubs2)

        createGameInfoRobot().assertCardsOnTable(emptyList())
        createPlayerInfoRobot(guestPlayer1Id)
            .assertCanPlay(true)

        // Play a card
        playCard(Card.Clubs6)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs6))
        createPlayerInfoRobot(guestPlayer4Id)
            .assertCanPlay(true)

        // Play a card
        playCard(Card.HeartsKing)

        createGameInfoRobot().assertCardsOnTable(listOf(Card.Clubs6, Card.HeartsKing))
        createPlayerInfoRobot(guestPlayer1Id)
            .assertCanPlay(true)

        // Pick a card
//        pickCard()

        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.Clubs6, Card.HeartsKing))
            .assertGameEvent(null)
        createPlayerInfoRobot(guestPlayer1Id)
            .assertCanPlay(true)

        // Pass turn
        passTurn()

        createGameInfoRobot()
            .assertCardsOnTable(emptyList())
            .assertGameEvent(DwitchGameEvent.TableHasBeenClearedTurnPassed)
            .assertGamePhase(DwitchGamePhase.RoundIsOnGoing)
        createPlayerInfoRobot(guestPlayer4Id)
            .assertCanPlay(true)

        // Play a card
        playCard(Card.SpadesAce)

        createGameInfoRobot()
            .assertCardsOnTable(listOf(Card.SpadesAce))
            .assertGameEvent(null)
            .assertGamePhase(DwitchGamePhase.RoundIsOver)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.ViceAsshole)
        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerRank(DwitchRank.President)
        createPlayerInfoRobot(guestPlayer3Id)
            .assertPlayerRank(DwitchRank.VicePresident)
        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerRank(DwitchRank.Asshole) // Finished with a Joker

        createPlayerInfoRobot(guestPlayer4Id)
            .assertCanPlay(false)
            .assertPlayerStatus(DwitchPlayerStatus.Done)
            .assertPlayerRank(DwitchRank.Neutral)
            .assertCanStartNewRound(true)
    }

    /**
     * Special rule: when playing on top of the first Jack played in the round (instead of "passing"), the player becomes the
     * asshole. It can be that another player breaks another special rule that make it the new asshole, though.
     */
    @Test
    fun `Player plays a card when last card is the first Jack played of the round and becomes the asshole`() {
        val hostCards = listOf(Card.Diamonds3, Card.Diamonds6)
        val guest1Cards = listOf(Card.Clubs4, Card.ClubsJack)
        val guest2Cards = listOf(Card.HeartsQueen, Card.Hearts3)

        val initialGameSetup = DeterministicInitialGameSetup(
            mapOf(
                0 to hostCards,
                1 to guest1Cards,
                2 to guest2Cards
            ),
            mapOf(
                0 to DwitchRank.Asshole,
                1 to DwitchRank.Neutral,
                2 to DwitchRank.President
            )
        )

        gameState = GameBootstrap.createNewGame(
            listOf(hostPlayer, guestPlayer1, guestPlayer2),
            initialGameSetup
        )

        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Host plays a card
        playCard(Card.Diamonds6)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest1 plays the first Jack of the round
        playCard(Card.ClubsJack)

        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest2 plays a card on top of the first Jack of the round --> will become the asshole unless another player breaks
        // another special rule before the end of the round
        playCard(Card.HeartsQueen)

        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Host picks a card and pass
//        pickCard()
        passTurn()

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest1 picks a card and pass
//        pickCard()
        passTurn()

        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest2 plays its last card and finishes the round
        playCard(Card.Hearts3)

        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Host plays its last cards and finishes the round
        playCard(Card.Diamonds3)
        playCard(Card.Clubs3)

        createGameInfoRobot().assertGamePhase(DwitchGamePhase.RoundIsOver)

        // It would the president if it hadn't broken the special rule under test
        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerRank(DwitchRank.Asshole)

        // It would normally be Neutral but is President because Guest2 broke the rule under test
        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerRank(DwitchRank.President)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerRank(DwitchRank.Neutral)
    }

    @Test
    fun `Player plays the first Jack of the round, all other players pass and no player break any other special rule hence ranks are given according to finishing order`() {
        val hostCards = listOf(Card.ClubsJack, Card.Diamonds6)
        val guest1Cards = listOf(Card.Clubs6, Card.Clubs2)
        val guest2Cards = listOf(Card.HeartsQueen, Card.Hearts3)

        val initialGameSetup = DeterministicInitialGameSetup(
            mapOf(
                0 to hostCards,
                1 to guest1Cards,
                2 to guest2Cards
            ),
            mapOf(
                0 to DwitchRank.Asshole,
                1 to DwitchRank.Neutral,
                2 to DwitchRank.President
            )
        )

        gameState = GameBootstrap.createNewGame(
            listOf(hostPlayer, guestPlayer1, guestPlayer2),
            initialGameSetup
        )

        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Host plays the first Jack of the round
        playCard(Card.ClubsJack)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest1 picks a card and passes
//        pickCard()
        passTurn()

        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest2 picks a card and passes
//        pickCard()
        passTurn()

        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Host plays its last card
        playCard(Card.Diamonds6)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerStatus(DwitchPlayerStatus.Playing)

        // Guest1 plays a card and dwitches Guest2
        playCard(Card.Clubs6)

        // Guest1 plays a joker
        playCard(Card.Clubs2)

        // Guest1 plays its last card
        playCard(Card.Clubs4)

        createGameInfoRobot().assertGamePhase(DwitchGamePhase.RoundIsOver)

        createPlayerInfoRobot(hostPlayerId)
            .assertPlayerRank(DwitchRank.President)

        createPlayerInfoRobot(guestPlayer1Id)
            .assertPlayerRank(DwitchRank.Neutral)

        createPlayerInfoRobot(guestPlayer2Id)
            .assertPlayerRank(DwitchRank.Asshole)
    }

    private fun playCard(card: Card) {
        gameState = DwitchEngineImpl(gameState).playCard(card)
    }

    private fun passTurn() {
        gameState = DwitchEngineImpl(gameState).passTurn()
    }

    private fun createPlayerInfoRobot(playerId: DwitchPlayerId): PlayerInfoRobot {
        return PlayerInfoRobot(DwitchEngineImpl(gameState).getGameInfo().playerInfos.getValue(playerId))
    }

    private fun createGameInfoRobot(): GameInfoRobot {
        return GameInfoRobot(DwitchEngineImpl(gameState).getGameInfo())
    }
}
