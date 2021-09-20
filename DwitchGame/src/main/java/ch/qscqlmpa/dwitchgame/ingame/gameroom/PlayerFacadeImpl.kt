package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@OngoingGameScope
internal class PlayerFacadeImpl @Inject constructor(
    private val gameCommunicator: GameCommunicator,
    private val gameRepository: GameRepository,
    private val gameInteractor: GameInteractor,
    private val schedulerFactory: SchedulerFactory
) : PlayerFacade, GameCommunicator {

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

    override fun getGameName(): Single<String> {
        return gameRepository.getGameName()
            .subscribeOn(schedulerFactory.io())
    }
}
