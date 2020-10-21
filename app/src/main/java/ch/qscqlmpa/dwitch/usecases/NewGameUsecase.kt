package ch.qscqlmpa.dwitch.usecases

import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitch.persistence.Store
import io.reactivex.Completable
import javax.inject.Inject

class NewGameUsecase @Inject
constructor(private val serviceManager: ServiceManager,
            private val store: Store
) {

    fun hostNewgame(gameName: String, playerName: String): Completable {
        return Completable.fromAction {
            val insertGameResult = store.insertGameForHost(gameName, playerName, "127.0.0.1", 8889) //TODO Set correct port
            serviceManager.startHostService(insertGameResult.gameLocalId, insertGameResult.localPlayerLocalId)
        }
    }

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Completable.fromAction {
            val insertGameResult = store.insertGameForGuest(
                    advertisedGame.name,
                    playerName,
                    advertisedGame.ipAddress,
                    advertisedGame.port
            )

            serviceManager.startGuestService(
                    insertGameResult.gameLocalId,
                    insertGameResult.localPlayerLocalId,
                    advertisedGame.port,
                    advertisedGame.ipAddress
            )
        }
    }
}
