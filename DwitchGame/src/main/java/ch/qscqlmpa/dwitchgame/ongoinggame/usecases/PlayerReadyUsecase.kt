package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class PlayerReadyUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator
) {

    fun updateReadyState(ready: Boolean): Completable {
        return Single.fromCallable {
            val localPlayerDwitchId = store.getLocalPlayerDwitchId()
            store.updatePlayerWithReady(localPlayerDwitchId, ready)
            GuestMessageFactory.createPlayerReadyMessage(localPlayerDwitchId, ready)
        }.flatMapCompletable(communicator::sendMessageToHost)
    }
}