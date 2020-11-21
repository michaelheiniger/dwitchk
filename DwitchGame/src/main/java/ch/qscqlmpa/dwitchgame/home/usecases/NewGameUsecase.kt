package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchstore.InsertGameResult
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class NewGameUsecase @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val store: Store
) {

    fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable {
        return Single.fromCallable { store.insertGameForHost(gameName, playerName) }
            .doAfterSuccess { insertGameResult -> startHostService(insertGameResult, gamePort) }
            .ignoreElement()
    }

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Single.fromCallable {
            store.insertGameForGuest(
                advertisedGame.gameName,
                advertisedGame.gameCommonId,
                playerName
            )
        }
            .doAfterSuccess { result -> startGuestService(result, advertisedGame) }
            .ignoreElement()
    }

    private fun startHostService(insertGameResult: InsertGameResult, port: Int) {
        appEventRepository.notify(AppEvent.GameCreated(GameCreatedInfo(insertGameResult, port)))
    }

    private fun startGuestService(insertGameResult: InsertGameResult, advertisedGame: AdvertisedGame) {
        appEventRepository.notify(AppEvent.GameJoined(GameJoinedInfo(insertGameResult, advertisedGame)))
    }
}
