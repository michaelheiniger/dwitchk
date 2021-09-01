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

internal class JoinNewGameUsecase @Inject constructor(
    private val applicationConfigRepository: ApplicationConfigRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val store: Store
) {
    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable = Completable.merge(
        listOf(
            createGame(advertisedGame, playerName),
            waitForJoinAckFromHost()
        )
    )

    private fun createGame(advertisedGame: AdvertisedGame, playerName: String) = Completable.fromAction {
        val result = store.insertGameForGuest(advertisedGame.gameName, advertisedGame.gameCommonId, playerName)
        guestGameLifecycleEventRepository.notify(
            GuestGameLifecycleEvent.GameSetup(GameJoinedInfo(result, advertisedGame))
        )
    }

    private fun waitForJoinAckFromHost() = guestGameLifecycleEventRepository.observeEvents()
        .filter { event -> event is GuestGameLifecycleEvent.GameJoined || event is GuestGameLifecycleEvent.GameRejoined }
        .firstElement()
        .ignoreElement()
        .timeout(applicationConfigRepository.config.communication.waitForJoinOrRejoinAckTimeout, TimeUnit.SECONDS)
}
