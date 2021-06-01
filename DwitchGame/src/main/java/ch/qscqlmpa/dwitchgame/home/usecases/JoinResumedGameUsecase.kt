package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class JoinResumedGameUsecase @Inject constructor(
    private val store: Store,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) {
    fun joinResumedGame(advertisedGame: AdvertisedGame): Completable {
        return Completable.fromAction {
            val game = store.getGame(advertisedGame.gameCommonId)!!
            store.updateCurrentRoom(game.id, RoomType.WAITING_ROOM)
            startGuestService(game, advertisedGame)
        }
    }

    private fun startGuestService(game: Game, advertisedGame: AdvertisedGame) {
        guestGameLifecycleEventRepository.notify(
            GuestGameLifecycleEvent.GameJoined(
                GameJoinedInfo(
                    game.id,
                    game.localPlayerLocalId,
                    advertisedGame.gameIpAddress,
                    advertisedGame.gamePort
                )
            )
        )
    }
}
