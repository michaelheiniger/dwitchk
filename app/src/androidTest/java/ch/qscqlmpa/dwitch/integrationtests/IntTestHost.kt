package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.IntTestWebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat

class IntTestHost(
    private val gameName: String,
    private val networkHub: NetworkHub
) : IntTestPlayer() {

    private lateinit var gameRoomHostFacade: GameRoomHostFacade

    private var hostLocalId: Long? = null
    lateinit var playerId: PlayerInGameId

    fun createGame() {
        appComponent.newGameUsecase.hostGame(gameName, "Aragorn").blockingGet()
        val game = appComponent.database.gameDao().getGameByName(gameName)
            ?: throw IllegalStateException("New game can't be fetched from store")
        gameLocalId = game.id
        hostLocalId = game.localPlayerLocalId
        playerId = appComponent.database.playerDao().gePlayer(hostLocalId!!).inGameId

        hookOnGoingGameComponent()
        hookupHostToNetworkHub()
    }

    fun gameCommonId(): GameCommonId {
        return ongoingGameComponent.inGameStore.getGame().gameCommonId
    }

    fun launchGame(initialGameSetup: InitialGameSetup) {
        val gameLaunchableEvent = ongoingGameComponent.waitingRoomHostFacade.gameCanBeLaunched()
            .blockingFirst()
        assertThat(gameLaunchableEvent).isEqualTo(GameLaunchableEvent.GameIsReadyToBeLaunched)

        // Order is according to their players' name (alphabetical, ascending)
        (ongoingGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
            .setInstance(initialGameSetup)

        ongoingGameComponent.waitingRoomHostFacade.launchGame()
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.trampoline())
            .blockingGet()
    }

    fun endGame() {
        gameRoomHostFacade.endGame().blockingGet()
    }

    fun assertGameOverReceived() {
        assertThat(gameRoomHostFacade.consumeLastEvent()).isEqualTo(GuestGameEvent.GameOver)
    }

    override fun hookOnGoingGameComponent() {
        super.hookOnGoingGameComponent()
        gameRoomHostFacade = ongoingGameComponent.gameRoomHostFacade
    }

    private fun hookupHostToNetworkHub() {
        val websocket = ongoingGameComponent.websocketServer as IntTestWebsocketServer
        networkHub.setHost(websocket)
        websocket.setNetworkHub(networkHub)
    }
}