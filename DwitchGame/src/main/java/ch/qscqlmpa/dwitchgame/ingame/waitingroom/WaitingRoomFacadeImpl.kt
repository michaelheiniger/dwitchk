package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class WaitingRoomFacadeImpl @Inject constructor(
    private val waitingRoomPlayerRepository: WaitingRoomPlayerRepository,
    private val store: InGameStore,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomFacade {

    override fun observePlayers(): Observable<List<PlayerWrUi>> {
        return waitingRoomPlayerRepository.observePlayers()
            .subscribeOn(schedulerFactory.io())
    }

    override fun gameInfo(): Single<GameInfoUi> {
        return Single.fromCallable { store.getGame() }
            .map { g -> GameInfoUi(g.name, g.isNew()) }
            .subscribeOn(schedulerFactory.io())
    }
}
