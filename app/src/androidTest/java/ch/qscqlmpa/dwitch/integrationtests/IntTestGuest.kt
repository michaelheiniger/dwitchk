package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomGuestFacade

class IntTestGuest : IntTestPlayer() {

    private lateinit var gameRoomGuestFacade: GameRoomGuestFacade

    //    private val guestLocalId: Long
    lateinit var playerId: DwitchPlayerId

    init {
//        appComponent.newGameUsecase.joinGame(advertisedGame, guest.name).blockingGet()
//        val game = appComponent.database.gameDao().getGameByName(advertisedGame.gameName)
//                ?: throw IllegalStateException("New game can't be fetched from store")
//        gameLocalId = game.id
//        guestLocalId = game.localPlayerLocalId

        hookOnGoingGameComponent()
    }

    fun joinGame() {
//        hookUpGuestToNetworkHub()
//        ongoingGameComponent.guestCommunicator.connect()
//        playerId = appComponent.database.playerDao().gePlayer(guestLocalId).inGameId
//        ongoingGameComponent.waitingRoomGuestFacade.updateReadyState(true).blockingGet()
    }

    fun assertGameOverReceived() {
//        Assertions.assertThat(gameRoomGuestFacade.consumeLastEvent()).isEqualTo(GuestGameEvent.GameOver)
    }

    //    private fun getWebsocketClient(): IntTestWebsocketClient {
//        return ongoingGameComponent.websocketClientFactory.create() as IntTestWebsocketClient
//    }

    private fun hookUpGuestToNetworkHub() {
//        val websocketClient = getWebsocketClient()
//        networkHub.addGuest(guest, websocketClient)
//        websocketClient.setNetworkHub(networkHub, guest)
    }
}
