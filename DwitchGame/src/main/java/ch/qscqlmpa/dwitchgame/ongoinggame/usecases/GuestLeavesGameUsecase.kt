package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
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
