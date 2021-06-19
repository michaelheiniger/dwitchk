package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class PlayerReadyUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator
) {

    fun updateReadyState(ready: Boolean): Completable {
        return Completable.fromAction {
            val localPlayerDwitchId = store.getLocalPlayerDwitchId()
            store.updatePlayerWithReady(localPlayerDwitchId, ready)
            val message = GuestMessageFactory.createPlayerReadyMessage(localPlayerDwitchId, ready)
            communicator.sendMessageToHost(message)
        }
    }
}
