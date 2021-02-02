package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CancelGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: HostCommunicator,
    private val appEventRepository: AppEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) {

    fun cancelGame(): Completable {
        return Completable.fromAction {
            if (store.gameIsNew()) {
                store.deleteGame()
            }
            sendCancelGameMessage()
            appEventRepository.notify(AppEvent.GameOver)
            gameEventRepository.notify(GuestGameEvent.GameCanceled)
        }
    }

    private fun sendCancelGameMessage() {
        val message = HostMessageFactory.createCancelGameMessage()
        communicator.sendMessage(message)
    }
}
