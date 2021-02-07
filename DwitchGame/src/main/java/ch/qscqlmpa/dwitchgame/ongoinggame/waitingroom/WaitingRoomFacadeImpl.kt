package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomFacadeImpl @Inject constructor(
    private val waitingRoomPlayerRepository: WaitingRoomPlayerRepository,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomFacade {

    override fun observePlayers(): Observable<List<PlayerWr>> {
        return waitingRoomPlayerRepository.observePlayers()
            .subscribeOn(schedulerFactory.io())
    }
}
