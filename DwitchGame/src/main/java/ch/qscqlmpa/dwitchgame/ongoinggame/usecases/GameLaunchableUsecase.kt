package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWr
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomPlayerRepository
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.Observable
import javax.inject.Inject

internal class GameLaunchableUsecase @Inject constructor(
    private val waitingRoomPlayerRepository: WaitingRoomPlayerRepository
) {

    fun gameCanBeLaunched(): Observable<GameLaunchableEvent> {
        return waitingRoomPlayerRepository.observePlayers()
            .map { players -> players.filter { p -> p.connectionState == PlayerConnectionState.CONNECTED } }
            .map { players ->
                if (players.size < 2) {
                    GameLaunchableEvent.NotEnoughPlayers
                } else {
                    when (playersAreAllReady(players)) {
                        true -> GameLaunchableEvent.GameIsReadyToBeLaunched
                        false -> GameLaunchableEvent.NotAllPlayersAreReady
                    }
                }
            }
    }

    private fun playersAreAllReady(playerList: List<PlayerWr>): Boolean {
        return playerList.size > 1 && playerList.fold(true, { acc, player -> acc && player.ready })
    }
}

sealed class GameLaunchableEvent {
    object GameIsReadyToBeLaunched : GameLaunchableEvent()
    object NotEnoughPlayers : GameLaunchableEvent()
    object NotAllPlayersAreReady : GameLaunchableEvent()
}