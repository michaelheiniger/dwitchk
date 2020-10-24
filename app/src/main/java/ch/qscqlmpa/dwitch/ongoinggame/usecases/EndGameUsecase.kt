package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.MessageFactory
import io.reactivex.Completable
import javax.inject.Inject

class EndGameUsecase @Inject constructor(
    private val gameEventRepository: GameEventRepository,
    private val communicator: GameCommunicator
) {

    fun endGame(): Completable {
        gameEventRepository.notifyOfEvent(GameEvent.GameOver)
        return communicator.sendMessage(MessageFactory.createGameOverMessage())
    }
}