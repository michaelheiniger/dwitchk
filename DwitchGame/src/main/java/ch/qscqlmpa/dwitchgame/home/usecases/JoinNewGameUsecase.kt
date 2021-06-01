package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class JoinNewGameUsecase @Inject constructor(
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val store: Store
) {
    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable {
        return Completable.fromAction {
            val result = store.insertGameForGuest(advertisedGame.gameName, advertisedGame.gameCommonId, playerName)
            guestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameJoined(GameJoinedInfo(result, advertisedGame)))
        }
    }
}
