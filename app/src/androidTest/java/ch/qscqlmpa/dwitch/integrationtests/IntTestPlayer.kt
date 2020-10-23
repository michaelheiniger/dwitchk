package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.DaggerIntTestAppComponent
import ch.qscqlmpa.dwitch.IntTestAppComponent
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.IntTestOngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameModule
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.game.GameInteractor
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.utils.PlayerDashboardRobot
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card

abstract class IntTestPlayer {

    protected val appComponent: IntTestAppComponent = DaggerIntTestAppComponent.builder().build()
    protected var gameLocalId: Long? = null
    protected lateinit var ongoingGameComponent: IntTestOngoingGameComponent
    private lateinit var inGameStore: InGameStore
    private lateinit var gameInteractor: GameInteractor
    protected lateinit var serializerFactory: SerializerFactory

    protected fun createOnGoingGameComponent(
        playerRole: PlayerRole,
        localPlayerLocalId: Long,
        hostAddress: String,
        hostPort: Int = 8889
    ) {
        ongoingGameComponent = appComponent.addInGameComponent(
            OngoingGameModule(
                playerRole,
                RoomType.WAITING_ROOM,
                gameLocalId!!,
                localPlayerLocalId,
                hostPort,
                hostAddress
            )
        )
        inGameStore = ongoingGameComponent.inGameStore
        gameInteractor = ongoingGameComponent.gameInteractor
        serializerFactory = ongoingGameComponent.serializerFactory
    }

    fun playCard(card: Card) {
        gameInteractor.playCard(card).blockingGet()
    }

    fun pickCard() {
        gameInteractor.pickCard().blockingGet()
    }

    fun passTurn() {
        gameInteractor.passTurn().blockingGet()
    }

    fun startNewRound() {
        gameInteractor.startNewRound().blockingGet()
    }

    fun assertDashboard(): PlayerDashboardRobot {
        val gameState = inGameStore.getGameState()
        println("gameState: $gameState")
        val engine = DwitchEngine(gameState)
        val localPlayerInGameId = inGameStore.getLocalPlayerInGameId()
        return PlayerDashboardRobot(engine.getPlayerDashboard(localPlayerInGameId))
    }
}