package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class CancelGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: HostCommunicator,
    private val appEventRepository: AppEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) {

    fun cancelGame(): Completable {
        return deleteGameFromStore()
            .andThen(sendCancelGameMessage())
            .doOnComplete {
                appEventRepository.notify(AppEvent.GameOver)
                gameEventRepository.notify(GuestGameEvent.GameCanceled)
            }
    }

    private fun deleteGameFromStore(): Completable {
        return Completable.fromAction { store.deleteGame() }
    }

    private fun sendCancelGameMessage(): Completable {
        return Single.fromCallable { HostMessageFactory.createCancelGameMessage() }
            .flatMapCompletable(communicator::sendMessage)
    }
}
