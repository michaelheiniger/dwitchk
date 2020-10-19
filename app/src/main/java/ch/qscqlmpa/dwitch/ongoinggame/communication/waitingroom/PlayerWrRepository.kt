package ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import io.reactivex.Observable
import javax.inject.Inject

@OngoingGameScope
class PlayerWrRepository @Inject constructor(private val store: InGameStore) {

    fun observeConnectedPlayers(): Observable<List<PlayerWr>> {
        return store.observeConnectedPlayers()
                .map(Companion::createPlayerWrList)
                .onBackpressureLatest()
                .toObservable()
    }

    companion object {
        private fun createPlayerWrList(players: List<Player>): List<PlayerWr> {
            return players.map { player -> PlayerWr(player.inGameId, player.name, player.ready) }
        }
    }
}