package ch.qscqlmpa.dwitchgame.ingame

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameFacadeImpl @Inject constructor(
    private val _localPlayerRole: PlayerRole,
    private val store: InGameStore,
    private val schedulerFactory: SchedulerFactory
) : GameFacade {
    override val localPlayerRole: PlayerRole get() = _localPlayerRole
    override fun observeCurrentRoom(): Observable<RoomType> = store.observeCurrentRoom().subscribeOn(schedulerFactory.io())
}