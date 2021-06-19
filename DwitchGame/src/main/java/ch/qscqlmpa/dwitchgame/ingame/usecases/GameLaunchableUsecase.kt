package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomPlayerRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameLaunchableUsecase @Inject constructor(
    private val waitingRoomPlayerRepository: WaitingRoomPlayerRepository
) {

    fun gameCanBeLaunched(): Observable<GameLaunchableEvent> {
        return waitingRoomPlayerRepository.observePlayers()
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

    private fun playersAreAllReady(playerList: List<PlayerWrUi>): Boolean {
        return playerList.size > 1 && playerList.fold(true, { acc, player -> acc && player.ready })
    }
}

sealed class GameLaunchableEvent(val launchable: Boolean) {
    object GameIsReadyToBeLaunched : GameLaunchableEvent(launchable = true)
    object NotEnoughPlayers : GameLaunchableEvent(launchable = false)
    object NotAllPlayersAreReady : GameLaunchableEvent(launchable = false)
}
