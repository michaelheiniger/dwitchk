package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class LeaveGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val serviceManager: ServiceManager,
    private val communicator: GuestCommunicator
) {

    fun leaveGame(): Completable {
        return buildLeaveGameMessage()
            .flatMapCompletable(communicator::sendMessage)
            .doOnComplete {
                communicator.closeConnection()
                serviceManager.stopGuestService()
            }
            .andThen(deleteGameFromStore())
    }

    private fun buildLeaveGameMessage(): Single<EnvelopeToSend> {
        return Single.fromCallable {
            GuestMessageFactory.createLeaveGameMessage(store.getLocalPlayerInGameId())
        }
    }

    private fun deleteGameFromStore(): Completable {
        return Completable.fromAction { store.deleteGame() }
    }
}