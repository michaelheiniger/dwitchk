package ch.qscqlmpa.dwitch.integrationtests

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitch.Guest1
import ch.qscqlmpa.dwitch.Guest2
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DwitchNoUiTest {

    private val gameName = "Dwiiiitch"
    private val gameIpAddress = "192.168.1.10"
    private val gamePort = 8889

    private lateinit var networkHub: NetworkHub

    @Before
    fun setup() {
        networkHub = NetworkHub()
    }

    /**
     * - Create game
     * - 2 guests join
     * - play 2 rounds
     * - end game
     */
    @Test
    fun playGameFromStartToEnd() {
        val host = IntTestHost(gameName, networkHub)
        host.createGame()

        val advertisedGame = AdvertisedGame(gameName, host.gameCommonId(),  gameIpAddress, gamePort)
        val guest1 = IntTestGuest(Guest1, advertisedGame, networkHub)
        val guest2 = IntTestGuest(Guest2, advertisedGame, networkHub)

        guest1.joinGame()
        guest2.joinGame()

        host.launchGame(buildInitialGameSetup())

        host.assertDashboard()
                .assertCanPlay(true)
                .assertGamePhase(GamePhase.RoundIsBeginning)
                .assertCanStartNewRound(false)

        guest1.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsBeginning)
                .assertCanStartNewRound(false)

        guest2.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsBeginning)
                .assertCanStartNewRound(false)

        host.playCard(Card.Clubs2)

        host.assertDashboard()
                .assertCanPlay(true)

        guest1.assertDashboard()
                .assertCanPlay(false)

        guest2.assertDashboard()
                .assertCanPlay(false)

        host.playCard(Card.Clubs3)

        host.assertDashboard()
                .assertCanPlay(false)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertCardsOnTable(listOf(Card.Clubs3))

        guest1.assertDashboard()
                .assertCanPlay(true)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertCardsOnTable(listOf(Card.Clubs3))

        guest2.assertDashboard()
                .assertCanPlay(false)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertCardsOnTable(listOf(Card.Clubs3))

        guest1.playCard(Card.Hearts4)

        host.assertDashboard()
                .assertCanPlay(false)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4))

        guest1.assertDashboard()
                .assertCanPlay(false)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4))

        guest2.assertDashboard()
                .assertCanPlay(true)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4))

        guest2.playCard(Card.Diamonds7)

        guest1.assertDashboard()
                .assertCanPlay(true)
                .assertCanPickACard(true)
                .assertCanPass(false)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4, Card.Diamonds7))

        guest2.assertDashboard()
                .assertCanPlay(false)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4, Card.Diamonds7))

        guest1.pickCard()

        guest1.assertDashboard()
                .assertCanPlay(true)
                .assertCanPickACard(false)
                .assertCanPass(true)
                .assertCardsInHandInAnyOrder(Card.Clubs5, Card.Diamonds6)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4, Card.Diamonds7))

        guest2.assertDashboard()
                .assertCanPlay(false)
                .assertCardsOnTable(listOf(Card.Clubs3, Card.Hearts4, Card.Diamonds7))

        guest1.passTurn()

        guest1.assertDashboard()
                .assertCanPlay(false)
                .assertCanPickACard(false)
                .assertCanPass(false)
                .assertTableEmpty()

        guest2.assertDashboard()
                .assertCanPlay(true)
                .assertTableEmpty()

        guest2.playCard(Card.Spades3)

        host.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsOver)
                .assertCanStartNewRound(true)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertPlayerState(guest1.playerId, PlayerState.Done)
                .assertPlayerState(guest2.playerId, PlayerState.Done)
                .assertPlayerRank(host.playerId, Rank.President)
                .assertPlayerRank(guest1.playerId, Rank.Asshole)
                .assertPlayerRank(guest2.playerId, Rank.Neutral)

        guest1.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsOver)
                .assertCanStartNewRound(true)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertPlayerState(guest1.playerId, PlayerState.Done)
                .assertPlayerState(guest2.playerId, PlayerState.Done)
                .assertPlayerRank(host.playerId, Rank.President)
                .assertPlayerRank(guest1.playerId, Rank.Asshole)
                .assertPlayerRank(guest2.playerId, Rank.Neutral)

        guest2.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsOver)
                .assertCanStartNewRound(true)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertPlayerState(guest1.playerId, PlayerState.Done)
                .assertPlayerState(guest2.playerId, PlayerState.Done)
                .assertPlayerRank(host.playerId, Rank.President)
                .assertPlayerRank(guest1.playerId, Rank.Asshole)
                .assertPlayerRank(guest2.playerId, Rank.Neutral)

        guest1.startNewRound()

        host.assertDashboard()
            .assertCanPlay(false)
            .assertGamePhase(GamePhase.RoundIsBeginning)

        guest1.assertDashboard()
            .assertCanPlay(true)
            .assertCardsInHandInAnyOrder(Card.Clubs3)
            .assertGamePhase(GamePhase.RoundIsBeginning)

        guest2.assertDashboard()
            .assertCanPlay(false)
            .assertGamePhase(GamePhase.RoundIsBeginning)

        guest1.playCard(Card.Clubs3)
        guest2.playCard(Card.Clubs4)

        host.assertDashboard()
            .assertCanPlay(false)
            .assertGamePhase(GamePhase.RoundIsOver)

        guest1.assertDashboard()
            .assertCanPlay(false)
            .assertGamePhase(GamePhase.RoundIsOver)

        guest2.assertDashboard()
            .assertCanPlay(false)
            .assertGamePhase(GamePhase.RoundIsOver)

        host.endGame()

        host.assertGameOverReceived()
        guest1.assertGameOverReceived()
        guest2.assertGameOverReceived()
    }

    private fun buildInitialGameSetup(): DeterministicInitialGameSetup {
        return DeterministicInitialGameSetup(
            mapOf(
                0 to listOf(Card.Clubs2, Card.Clubs3), // Host
                1 to listOf(Card.Diamonds6, Card.Hearts4), // Guest1
                2 to listOf(Card.Diamonds7, Card.Spades3) // Guest2
            ),
            mapOf(
                0 to Rank.Asshole,
                1 to Rank.Neutral,
                2 to Rank.President
            )
        )
    }
}