package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class JoinResumedGameUsecase @Inject constructor(
    private val store: Store,
    private val applicationConfigRepository: ApplicationConfigRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) {
    fun joinResumedGame(advertisedGame: AdvertisedGame): Completable = Completable.merge(
        listOf(
            prepareGame(advertisedGame),
            waitForRejoinAckFromHost()
        )
    )

    private fun prepareGame(advertisedGame: AdvertisedGame) = Completable.fromAction {
        val game = store.getGame(advertisedGame.gameCommonId)!!
        store.preparePlayersForGameResume(game.id)
        guestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameSetup(GameJoinedInfo(game, advertisedGame)))
    }

    private fun waitForRejoinAckFromHost() = guestGameLifecycleEventRepository.observeEvents()
        .filter { event -> event is GuestGameLifecycleEvent.GameJoined || event is GuestGameLifecycleEvent.GameRejoined }
        .firstElement()
        .timeout(applicationConfigRepository.config.communication.waitForJoinOrRejoinAckTimeout, TimeUnit.SECONDS)
        .ignoreElement()
}
