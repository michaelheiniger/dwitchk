package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@OngoingGameScope
internal class WaitingRoomPlayerRepository @Inject constructor(
    private val store: InGameStore,
    private val localPlayerRole: PlayerRole
) {

    fun observePlayers(): Observable<List<PlayerWrUi>> {
        return Flowable.combineLatest(
            Flowable.fromCallable { store.gameIsNew() },
            store.observePlayersInWaitingRoom(),
            { gameIsNew, players -> players.map { p -> toPlayerWrUi(p, gameIsNew) } }
        )
            .onBackpressureLatest()
            .toObservable()
    }

    fun observeLocalPlayer(): Observable<PlayerWrUi> {
        // Local player is never kickable: either it's the host or it's a guest and guests cannot kick anyone.
        return store.observeLocalPlayer().map { p -> PlayerWrUi(p.id, p.name, p.connectionState, p.ready, kickable = false) }
    }

    private fun toPlayerWrUi(player: Player, gameIsNew: Boolean): PlayerWrUi {
        return PlayerWrUi(player.id, player.name, player.connectionState, player.ready, playerIsKickable(player, gameIsNew))
    }

    private fun playerIsKickable(player: Player, gameIsNew: Boolean): Boolean {
        return localPlayerRole.isHost() && player.isGuest && gameIsNew // Cannot kick a player off a resumed game
    }
}
