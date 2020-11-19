package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.DaggerIntTestAppComponent
import ch.qscqlmpa.dwitch.IntTestAppComponent
import ch.qscqlmpa.dwitch.ongoinggame.IntTestOngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.IntTestServiceManager
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.game.PlayerDashboardFacade
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.utils.PlayerDashboardRobot
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import org.assertj.core.api.Assertions.assertThat

abstract class IntTestPlayer {

    protected val appComponent: IntTestAppComponent = DaggerIntTestAppComponent.builder().build()
    protected var gameLocalId: Long? = null
    protected lateinit var ongoingGameComponent: IntTestOngoingGameComponent
    private lateinit var inGameStore: InGameStore
    private lateinit var playerDashboardFacade: PlayerDashboardFacade
    protected lateinit var serializerFactory: SerializerFactory

    private val serviceManager = appComponent.serviceManager as IntTestServiceManager

    init {
        serviceManager.setAppComponent(appComponent)
    }

    protected fun hookOnGoingGameComponent() {
        ongoingGameComponent = serviceManager.getOnGoingGameComponent()
        inGameStore = ongoingGameComponent.inGameStore
        playerDashboardFacade = ongoingGameComponent.playerDashboardFacade
        serializerFactory = ongoingGameComponent.serializerFactory
    }

    fun playCard(card: Card) {
        playerDashboardFacade.playCard(card).blockingGet()
    }

    fun pickCard() {
        playerDashboardFacade.pickCard().blockingGet()
    }

    fun passTurn() {
        playerDashboardFacade.passTurn().blockingGet()
    }

    fun startNewRound() {
        // Order is according to players' rank
        (ongoingGameComponent.cardDealerFactory as DeterministicCardDealerFactory).setInstance(
            DeterministicCardDealer(
                mapOf(
                    0 to listOf(Card.Clubs3), // Guest1
                    1 to listOf(Card.Clubs4), // Guest2
                    2 to listOf(Card.Clubs5) // Host
                )
            )
        )
        playerDashboardFacade.startNewRound().blockingGet()
    }

    fun assertGameOverReceived() {
        assertThat(ongoingGameComponent.gameRoomGuestFacade.consumeLastEvent()).isEqualTo(GuestGameEvent.GameOver)
    }

    fun assertDashboard(): PlayerDashboardRobot {
        val gameState = inGameStore.getGameState()
        println("gameState: $gameState")
        val engine = DwitchEngine(gameState)
        val localPlayerInGameId = inGameStore.getLocalPlayerInGameId()
        return PlayerDashboardRobot(engine.getPlayerDashboard(localPlayerInGameId))
    }
}