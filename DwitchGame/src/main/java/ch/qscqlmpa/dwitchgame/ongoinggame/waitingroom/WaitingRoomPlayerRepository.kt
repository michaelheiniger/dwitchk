package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@OngoingGameScope
internal class WaitingRoomPlayerRepository @Inject constructor(private val store: InGameStore) {

    fun observePlayers(): Observable<List<PlayerWrUi>> {
        return store.observePlayersInWaitingRoom()
            .map { players -> players.map(::toPlayerWrUi) }
            .onBackpressureLatest()
            .toObservable()
    }

    fun observeLocalPlayer(): Observable<PlayerWrUi> {
        return store.observeLocalPlayer().map(::toPlayerWrUi)
    }

    private fun toPlayerWrUi(player: Player): PlayerWrUi {
        return PlayerWrUi(player.name, player.connectionState, player.ready)
    }
}
