package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.PlayerHostTest
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.IntTestWebsocketClient
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

class IntTestGuest(
    private val guest: PlayerHostTest,
    advertisedGame: AdvertisedGame,
    private val networkHub: NetworkHub
) : IntTestPlayer() {

    private val guestLocalId: Long
    lateinit var playerId: PlayerInGameId

    init {
        appComponent.newGameUsecase.joinGame(advertisedGame, guest.name).blockingGet()
        val game = appComponent.database.gameDao().getGameByName(advertisedGame.gameName)
                ?: throw IllegalStateException("New game can't be fetched from store")
        gameLocalId = game.id
        guestLocalId = game.localPlayerLocalId

        hookOnGoingGameComponent()
    }

    fun joinGame() {
        hookUpGuestToNetworkHub()
        ongoingGameComponent.guestCommunicator.connect()
        playerId = appComponent.database.playerDao().getLocalPlayer(guestLocalId).inGameId
        ongoingGameComponent.playerReadyUsecase.updateReadyState(true).blockingGet()
    }

    private fun getWebsocketClient(): IntTestWebsocketClient {
        return ongoingGameComponent.websocketClient as IntTestWebsocketClient
    }

    private fun hookUpGuestToNetworkHub() {
        val websocketClient = getWebsocketClient()
        networkHub.addGuest(guest, websocketClient)
        websocketClient.setNetworkHub(networkHub, guest)
    }
}