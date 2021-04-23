package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class NewGameUsecase @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val store: Store
) {

    fun hostGame(gameName: String, playerName: String, gamePort: Int): Completable {
        return Completable.fromAction {
            val result = store.insertGameForHost(gameName, playerName)
            appEventRepository.notify(AppEvent.GameCreated(GameCreatedInfo(result, gamePort)))
        }
    }

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Completable.fromAction {
            val result = store.insertGameForGuest(advertisedGame.gameName, advertisedGame.gameCommonId, playerName)
            appEventRepository.notify(AppEvent.GameJoined(GameJoinedInfo(result, advertisedGame)))
        }
    }
}
