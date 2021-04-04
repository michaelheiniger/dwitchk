package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.EntityMapper
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@OngoingGameScope
internal class WaitingRoomPlayerRepository @Inject constructor(private val store: InGameStore) {

    fun observePlayers(): Observable<List<PlayerWrUi>> {
        return store.observePlayersInWaitingRoom()
            .map { players -> players.map(EntityMapper::toPlayerWrUi) }
            .onBackpressureLatest()
            .toObservable()
    }

    fun observeLocalPlayer(): Observable<PlayerWrUi> {
        return store.observeLocalPlayer().map(EntityMapper::toPlayerWrUi)
    }
}
