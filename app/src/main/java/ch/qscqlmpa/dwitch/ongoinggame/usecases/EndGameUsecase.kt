package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import javax.inject.Inject

internal class EndGameUsecase @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val serviceManager: ServiceManager,
    private val communicator: GameCommunicator
) {

    fun endGame(): Completable {
        return communicator.sendMessage(MessageFactory.createGameOverMessage())
            .doOnComplete {
                serviceManager.stopHostService()
                gameEventRepository.notify(GuestGameEvent.GameOver)
            }
    }
}