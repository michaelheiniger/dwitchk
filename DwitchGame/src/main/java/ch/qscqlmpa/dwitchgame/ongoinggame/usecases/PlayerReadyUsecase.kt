package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
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
