package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class EndOfRoundManager @Inject constructor(
    private val facade: GameFacade
) {

    fun observeScreenInfo(): Observable<GameRoomScreen.EndOfRound> {
        return facade.observeGameData()
            .filter { data -> data is DwitchState.EndOfRound }
            .map { data ->
                val endOfRoundInfo = (data as DwitchState.EndOfRound).info
                val playersSortedByRankASc =
                    endOfRoundInfo.playersInfo.sortedWith { p1, p2 -> -p1.rank.value.compareTo(p2.rank.value) }
                GameRoomScreen.EndOfRound(endOfRoundInfo.copy(playersInfo = playersSortedByRankASc))
            }
    }
}