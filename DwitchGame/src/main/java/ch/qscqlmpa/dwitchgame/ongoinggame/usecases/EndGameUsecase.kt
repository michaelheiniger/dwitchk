package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class EndGameUsecase @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val appEventRepository: AppEventRepository,
    private val communicator: GameCommunicator
) {

    fun endGame(): Completable {
        return communicator.sendMessage(MessageFactory.createGameOverMessage())
            .doOnComplete {
                appEventRepository.notify(AppEvent.GameOver)
                gameEventRepository.notify(GuestGameEvent.GameOver)
            }
    }
}