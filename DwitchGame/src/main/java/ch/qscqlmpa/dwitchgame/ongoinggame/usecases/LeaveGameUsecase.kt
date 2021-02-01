package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class LeaveGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val appEventRepository: AppEventRepository,
    private val communicator: GuestCommunicator
) {

    fun leaveGame(): Completable {
        return Single.fromCallable { store.gameIsNew() }
            .flatMapCompletable { gameIsNew ->
                if (gameIsNew) {
                    buildLeaveGameMessage()
                        .flatMapCompletable(communicator::sendMessageToHost)
                        .andThen(deleteGameFromStore())
                } else {
                    Completable.complete()
                }
            }
            .doOnComplete {
                communicator.closeConnection()
                appEventRepository.notify(AppEvent.GameLeft)
            }
    }

    private fun buildLeaveGameMessage(): Single<Message> {
        return Single.fromCallable { GuestMessageFactory.createLeaveGameMessage(store.getLocalPlayerDwitchId()) }
    }

    private fun deleteGameFromStore(): Completable {
        return Completable.fromAction { store.deleteGame() }
    }
}