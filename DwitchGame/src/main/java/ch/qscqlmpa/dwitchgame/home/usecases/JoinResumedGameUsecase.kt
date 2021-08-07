package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
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
            store.preparePlayersForGameResume(game.id)
            guestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameJoined(GameJoinedInfo(game, advertisedGame)))
        }
    }
}
