package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GuestLeavesGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val gameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val communicator: GuestCommunicator
) {

    fun leaveGame(): Completable {
        return Completable.fromAction {
            if (store.gameIsNew()) {
                val message = GuestMessageFactory.createLeaveGameMessage(store.getLocalPlayerDwitchId())
                communicator.sendMessageToHost(message)
                store.deleteGame()
            }
            gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GuestLeftGame)
        }
    }
}
