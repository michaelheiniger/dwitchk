package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameFacadeImpl @Inject constructor(
    private val gameCommunicator: GameCommunicator,
    private val gameRepository: GameRepository,
    private val gameInteractor: GameInteractor,
    private val schedulerFactory: SchedulerFactory
) : GameFacade, GameCommunicator {

    override fun sendMessageToHost(message: Message) {
        gameCommunicator.sendMessageToHost(message)
    }

    override fun observeGameData(): Observable<DwitchState> {
        return gameRepository.observeGameData()
            .subscribeOn(schedulerFactory.io())
    }

    override fun performAction(action: GameAction): Completable {
        return gameInteractor.performAction(action)
            .subscribeOn(schedulerFactory.io())
    }
}
