package ch.qscqlmpa.dwitch.usecases

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
        return Single.fromCallable {
            store.insertGameForHost(
                gameName,
                playerName,
                "127.0.0.1",
                8889 //TODO Set correct port
            )
        }
            .doAfterSuccess { insertGameResult ->
                serviceManager.startHostService(
                    insertGameResult.gameLocalId,
                    insertGameResult.localPlayerLocalId
                )
            }
            .ignoreElement()
    }

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Single.fromCallable {
            store.insertGameForGuest(
                advertisedGame.name,
                playerName,
                advertisedGame.ipAddress,
                advertisedGame.port
            )
        }
            .doAfterSuccess { insertGameResult ->
                serviceManager.startGuestService(
                    insertGameResult.gameLocalId,
                    insertGameResult.localPlayerLocalId,
//                    advertisedGame.port, //FIXME: it isn't the same port as the one advertised...
                    8889,
                    advertisedGame.ipAddress
                )
            }
            .ignoreElement()
    }
}
