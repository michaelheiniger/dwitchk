package ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import io.reactivex.Observable
import javax.inject.Inject

@OngoingGameScope
class WaitingRoomPlayerRepository @Inject constructor(private val store: InGameStore) {

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