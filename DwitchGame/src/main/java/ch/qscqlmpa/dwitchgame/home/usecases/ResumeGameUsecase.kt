package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ResumeGameUsecase @Inject constructor(
    private val store: Store,
    private val appEventRepository: AppEventRepository
) {

    fun joinResumedGame(advertisedGame: AdvertisedGame): Completable {
        return Completable.fromAction {
            val game = store.getGame(advertisedGame.gameCommonId)!!
            store.updateCurrentRoom(game.id, RoomType.WAITING_ROOM)
            startGuestService(game, advertisedGame)
        }
    }

    fun hostResumedGame(gameId: Long, gamePort: Int): Completable {
        return Completable.fromAction {
            val game = store.getGame(gameId)
            store.updateCurrentRoom(gameId, RoomType.WAITING_ROOM)
            store.prepareGuestsForGameResume(gameId) //TODO: find better name for method
            startHostService(game, gamePort)
        }
    }

    private fun startHostService(game: Game, gamePort: Int) {
        appEventRepository.notify(AppEvent.GameCreated(GameCreatedInfo(
            game.isNew(),
            game.id,
            game.gameCommonId,
            game.name,
            game.localPlayerLocalId,
            gamePort
        )))
    }

    private fun startGuestService(game: Game, advertisedGame: AdvertisedGame) {
        appEventRepository.notify(AppEvent.GameJoined(GameJoinedInfo(
            game.id,
            game.localPlayerLocalId,
            advertisedGame.gameIpAddress,
            advertisedGame.gamePort
        )))
    }
}