package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.Observable
import javax.inject.Inject

@OngoingGameScope
internal class WaitingRoomPlayerRepository @Inject constructor(private val store: InGameStore) {

    fun observePlayers(): Observable<List<PlayerWr>> {
        return store.observePlayersInWaitingRoom()
                .map { players -> players.map(::PlayerWr)}
                .onBackpressureLatest()
                .toObservable()
    }

    fun observeLocalPlayer(): Observable<PlayerWr> {
        return store.observeLocalPlayer().map(::PlayerWr)
    }
}