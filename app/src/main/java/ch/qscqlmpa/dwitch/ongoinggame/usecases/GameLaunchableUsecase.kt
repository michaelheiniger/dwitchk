package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWrRepository
import io.reactivex.Observable
import javax.inject.Inject

class GameLaunchableUsecase @Inject constructor(
        private val playerWrRepository: PlayerWrRepository
) {

    fun gameCanBeLaunched(): Observable<GameLaunchableEvent> {
        return playerWrRepository.observeConnectedPlayers()
                .map { playerList ->
                    return@map if (playerList.size < 2) {
                        GameLaunchableEvent.NotEnoughPlayers
                    } else {
                        when (playersAreAllReady(playerList)) {
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