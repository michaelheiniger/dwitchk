package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

class IntTestHost(
    private val gameName: String
) : IntTestPlayer() {

    private lateinit var inGameHostFacade: InGameHostFacade

    private var hostLocalId: Long? = null
    lateinit var playerId: DwitchPlayerId

    fun createGame() {
//        appComponent.newGameUsecase.hostGame(gameName, "Aragorn").blockingGet()
//        val game = appComponent.database.gameDao().getGameByName(gameName)
//            ?: throw IllegalStateException("New game can't be fetched from store")
//        gameLocalId = game.id
//        hostLocalId = game.localPlayerLocalId
//        playerId = appComponent.database.playerDao().gePlayer(hostLocalId!!).inGameId
//
//        hookOnGoingGameComponent()
//        hookupHostToNetworkHub()
    }

    fun gameCommonId(): GameCommonId {
//        return ongoingGameComponent.inGameStore.getGame().gameCommonId
        TODO()
    }

//    fun launchGame(initialGameSetup: InitialGameSetup) {
//        val gameLaunchableEvent = ongoingGameComponent.waitingRoomHostFacade.gameCanBeLaunched()
//            .blockingFirst()
//        assertThat(gameLaunchableEvent).isEqualTo(GameLaunchableEvent.GameIsReadyToBeLaunched)
//
//        // Order is according to their players' name (alphabetical, ascending)
//        (ongoingGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
//            .setInstance(initialGameSetup)
//
//        ongoingGameComponent.waitingRoomHostFacade.launchGame()
//            .observeOn(Schedulers.trampoline())
//            .blockingGet()
//    }

    fun endGame() {
        inGameHostFacade.endGame().blockingSubscribe()
    }

    fun assertGameOverReceived() {
//        assertThat(gameRoomHostFacade.consumeLastEvent()).isEqualTo(GuestGameEvent.GameOver)
    }

    override fun hookOnGoingGameComponent() {
//        super.hookOnGoingGameComponent()
//        gameRoomHostFacade = ongoingGameComponent.gameRoomHostFacade
    }

    private fun hookupHostToNetworkHub() {
//        val websocket = ongoingGameComponent.websocketServer as IntTestWebsocketServer
//        networkHub.setHost(websocket)
//        websocket.setNetworkHub(networkHub)
    }
}
