package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameRoomGuestFacade
import org.assertj.core.api.Assertions

class IntTestGuest(
    private val guest: PlayerHostTest,
    advertisedGame: AdvertisedGame
) : IntTestPlayer() {

    private lateinit var gameRoomGuestFacade: GameRoomGuestFacade

//    private val guestLocalId: Long
    lateinit var playerId: PlayerInGameId

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
        Assertions.assertThat(gameRoomGuestFacade.consumeLastEvent()).isEqualTo(GuestGameEvent.GameOver)
    }

    override fun hookOnGoingGameComponent() {
        super.hookOnGoingGameComponent()
//        gameRoomGuestFacade = ongoingGameComponent.gameRoomGuestFacade
    }

//    private fun getWebsocketClient(): IntTestWebsocketClient {
//        return ongoingGameComponent.websocketClientFactory.create() as IntTestWebsocketClient
//        TODO()
//    }

    private fun hookUpGuestToNetworkHub() {
//        val websocketClient = getWebsocketClient()
//        networkHub.addGuest(guest, websocketClient)
//        websocketClient.setNetworkHub(networkHub, guest)
    }
}