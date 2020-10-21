package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.GuestIdTestHost
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.IntTestWebsocketClient
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

class IntTestGuest(guest: GuestIdTestHost, advertisedGame: AdvertisedGame) : IntTestPlayer() {

    private val guestLocalId: Long
    lateinit var playerId: PlayerInGameId

    init {
        appComponent.newGameUsecase.joinGame(advertisedGame, guest.name).blockingGet()
        val game = appComponent.database.gameDao().getGameByName(advertisedGame.name)
                ?: throw IllegalStateException("New game can't be fetched from store")
        gameLocalId = game.id
        guestLocalId = game.localPlayerLocalId

        createOnGoingGameComponent(PlayerRole.GUEST, guestLocalId, advertisedGame.ipAddress)
    }

    fun joinGame() {
        ongoingGameComponent.guestCommunication.connect()
        playerId = appComponent.database.playerDao().getLocalPlayer(guestLocalId).inGameId
        ongoingGameComponent.playerReadyUsecase.updateReadyState(true).blockingGet()
    }

    fun getWebsocketClient(): IntTestWebsocketClient {
        return ongoingGameComponent.websocketClient as IntTestWebsocketClient
    }
}