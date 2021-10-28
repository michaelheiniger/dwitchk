package ch.qscqlmpa.dwitchgame.game.usecases

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class JoinResumedGameUsecase @Inject constructor(
    private val store: Store,
    private val applicationConfigRepository: ApplicationConfigRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) {
    fun joinResumedGame(advertisedGame: GameAdvertisingInfo): Single<RoomType> = Single.zip(
        prepareGame(advertisedGame).toSingleDefault("irrelevant"),
        waitForRejoinAckFromHost()
    ) { _, currentRoom -> currentRoom }

    private fun prepareGame(advertisedGame: GameAdvertisingInfo) = Completable.fromAction {
        val game = store.getGame(advertisedGame.gameCommonId)
            ?: throw IllegalArgumentException("Game cannot be found: $advertisedGame (is player a member of this game ?)")
        store.preparePlayersForGameResume(game.id)
        guestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameSetup(GameJoinedInfo(game, advertisedGame)))
    }

    private fun waitForRejoinAckFromHost() = guestGameLifecycleEventRepository.observeEvents()
        .filter { event -> event is GuestGameLifecycleEvent.GameJoined || event is GuestGameLifecycleEvent.GameRejoined }
        .firstOrError()
        .timeout(applicationConfigRepository.config.communication.waitForJoinOrRejoinAckTimeout, TimeUnit.SECONDS)
        .map { event ->
            when (event) {
                GuestGameLifecycleEvent.GameJoined -> RoomType.WAITING_ROOM
                is GuestGameLifecycleEvent.GameRejoined -> event.currenRoom
                else -> throw IllegalArgumentException("Only events ${GuestGameLifecycleEvent.GameJoined::class.java} and ${GuestGameLifecycleEvent.GameRejoined::class.java} are allowed.")
            }
        }
}
