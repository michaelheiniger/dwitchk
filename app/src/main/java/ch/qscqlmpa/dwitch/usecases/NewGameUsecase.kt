package ch.qscqlmpa.dwitch.usecases

import ch.qscqlmpa.dwitch.gameadvertising.GameInfo
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitch.persistence.Store
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class NewGameUsecase @Inject
constructor(
    private val serviceManager: ServiceManager,
    private val store: Store
) {

    fun hostNewgame(gameName: String, playerName: String): Completable {
        val hostPort = 8889 //TODO: get port from sharedpref
        return Single.fromCallable { store.insertGameForHost(gameName, playerName) }
            .doAfterSuccess { insertGameResult ->
                serviceManager.startHostService(
                    insertGameResult.gameLocalId,
                    GameInfo(insertGameResult.gameCommonId, insertGameResult.gameName, hostPort),
                    insertGameResult.localPlayerLocalId
                )
            }.ignoreElement()
    }

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Single.fromCallable {
            store.insertGameForGuest(advertisedGame.gameName, advertisedGame.gameCommonId, playerName)
        }.doAfterSuccess { insertGameResult ->
            serviceManager.startGuestService(
                insertGameResult.gameLocalId,
                insertGameResult.localPlayerLocalId,
                advertisedGame.gamePort,
                advertisedGame.gameIpAddress
            )
        }.ignoreElement()
    }
}
