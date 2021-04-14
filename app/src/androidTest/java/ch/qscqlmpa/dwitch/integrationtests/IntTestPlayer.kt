package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.gameadvertising.SerializerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore

abstract class IntTestPlayer {

//    protected val appComponent: IntTestAppComponent = DaggerIntTestAppComponent.builder().build()
    protected var gameLocalId: Long? = null

    //    protected lateinit var ongoingGameComponent: IntTestOngoingGameComponent
    private lateinit var inGameStore: InGameStore
    private lateinit var playerFacade: GameFacade
    protected lateinit var serializerFactory: SerializerFactory

//    private val serviceManager = appComponent.serviceManager as IntTestServiceManager

    init {
//        serviceManager.setAppComponent(appComponent)
    }

    protected open fun hookOnGoingGameComponent() {
//        ongoingGameComponent = serviceManager.getOnGoingGameComponent()
//        inGameStore = ongoingGameComponent.inGameStore
//        playerDashboardFacade = ongoingGameComponent.playerDashboardFacade
//        serializerFactory = ongoingGameComponent.serializerFactory
    }

    fun playCard(card: Card) {
        playerFacade.playCard(card).blockingSubscribe()
    }

    fun passTurn() {
        playerFacade.passTurn().blockingSubscribe()
    }

    fun startNewRound() {
        // Order is according to players' rank
//        (ongoingGameComponent.cardDealerFactory as DeterministicCardDealerFactory).setInstance(
//            DeterministicCardDealer(
//                mapOf(
//                    0 to listOf(Card.Clubs3), // Guest1
//                    1 to listOf(Card.Clubs4), // Guest2
//                    2 to listOf(Card.Clubs5) // Host
//                )
//            )
//        )
        playerFacade.startNewRound().blockingSubscribe()
    }

//    fun assertDashboard(): PlayerDashboardRobot {
//        val gameState = inGameStore.getGameState()
//        val engine = DwitchEngineImpl(gameState)
//        val localPlayerInGameId = inGameStore.getLocalPlayerInGameId()
//        return PlayerDashboardRobot(engine.getPlayerDashboard(localPlayerInGameId))
//    }
}
