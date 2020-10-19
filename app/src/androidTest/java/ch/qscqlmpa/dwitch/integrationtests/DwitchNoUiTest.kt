package ch.qscqlmpa.dwitch.integrationtests

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitch.Guest1
import ch.qscqlmpa.dwitch.Guest2
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DwitchNoUiTest {

    private val gameName = "Dwiiiitch"

    private val advertisedGame = AdvertisedGame(gameName, "192.168.1.10", 8889)

    private lateinit var networkHub: NetworkHub

    @Before
    fun setup() {
    }

    @Test
    fun playGameAsAsHost() {

        val host = IntTestHost(gameName)
        host.createGame()

        val guest1 = IntTestGuest(Guest1, advertisedGame)
        val guest2 = IntTestGuest(Guest2, advertisedGame)

        hookUpHostAndGuests(host, guest1, guest2)

        guest1.joinGame()
        guest2.joinGame()

        host.launchGame()

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

        guest1.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsOver)
                .assertCanStartNewRound(true)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertPlayerState(guest1.playerId, PlayerState.Done)
                .assertPlayerState(guest2.playerId, PlayerState.Done)

        guest2.assertDashboard()
                .assertCanPlay(false)
                .assertGamePhase(GamePhase.RoundIsOver)
                .assertCanStartNewRound(true)
                .assertPlayerState(host.playerId, PlayerState.Done)
                .assertPlayerState(guest1.playerId, PlayerState.Done)
                .assertPlayerState(guest2.playerId, PlayerState.Done)

        guest1.startNewRound()


    }

    private fun hookUpHostAndGuests(host: IntTestHost, guest1: IntTestGuest, guest2: IntTestGuest) {
        networkHub = NetworkHub(
                host.getWebsocketServer(),
                mapOf(
                        Guest1 to guest1.getWebsocketClient(),
                        Guest2 to guest2.getWebsocketClient()
                )
        )
        host.getWebsocketServer().setNetworkHub(networkHub)
        guest1.getWebsocketClient().setNetworkHub(networkHub, Guest1)
        guest2.getWebsocketClient().setNetworkHub(networkHub, Guest2)
    }
}