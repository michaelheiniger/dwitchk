package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@InGameScope
internal class WaitingRoomPlayerRepository @Inject constructor(
    private val store: InGameStore,
    private val localPlayerRole: PlayerRole,
    private val communicationStateRepository: CommunicationStateRepository
) {

    fun observePlayers(): Observable<List<PlayerWrUi>> {
        return Observable.combineLatest(
            store.observePlayersInWaitingRoom(),
            communicationStateRepository.connectedToGame(),
            Observable.fromCallable { store.gameIsNew() }
        ) { players, localPlayerConnected, gameIsNew -> players.map { p -> toPlayerWrUi(p, localPlayerConnected, gameIsNew) } }
            .distinctUntilChanged()
    }

    fun observeLocalPlayer(): Observable<PlayerWrUi> {
        // Local player is never kickable: either it's the host or it's a guest and guests cannot kick anyone.
        return store.observeLocalPlayer().map { p ->
            PlayerWrUi(
                id = p.id,
                name = p.name,
                connected = p.connected,
                ready = p.ready,
                kickable = false
            )
        }
            .distinctUntilChanged()
    }

    private fun toPlayerWrUi(player: Player, localPlayerConnected: Boolean, gameIsNew: Boolean): PlayerWrUi {
        return PlayerWrUi(
            id = player.id,
            name = player.name,
            connected = player.connected && localPlayerConnected,
            ready = player.ready,
            kickable = playerIsKickable(player, gameIsNew)
        )
    }

    private fun playerIsKickable(player: Player, gameIsNew: Boolean): Boolean {
        return localPlayerRole.isHost() && player.isGuest && gameIsNew // Cannot kick a player off a resumed game
    }
}
