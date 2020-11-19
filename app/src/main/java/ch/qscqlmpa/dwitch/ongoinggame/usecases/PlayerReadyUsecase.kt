package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class PlayerReadyUsecase @Inject constructor(private val store: InGameStore,
                                             private val communicator: GuestCommunicator
) {

    fun updateReadyState(ready: Boolean): Completable {
        return Single.fromCallable {
            val localPlayerInGameId = store.getLocalPlayerInGameId()
            store.updatePlayerWithReady(localPlayerInGameId, ready)
            GuestMessageFactory.createPlayerReadyMessage(localPlayerInGameId, ready)
        }.flatMapCompletable(communicator::sendMessage)
    }
}