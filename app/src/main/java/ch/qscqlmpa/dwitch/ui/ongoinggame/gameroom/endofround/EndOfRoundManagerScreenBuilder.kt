package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.GameRoomScreenBuilder
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.EndOfRoundInfo
import org.tinylog.kotlin.Logger

class EndOfRoundManagerScreenBuilder constructor(
    private val endOfRoundInfo: EndOfRoundInfo
) : GameRoomScreenBuilder {

    override val screen get() = GameRoomScreen.EndOfRound(endOfRoundInfo.copy(playersInfo = sortPlayersByRankDesc(endOfRoundInfo)))

    init {
        Logger.debug { "Create new ScreenBuilder ($this)" }
    }

    private fun sortPlayersByRankDesc(endOfRoundInfo: EndOfRoundInfo) = endOfRoundInfo.playersInfo
        .sortedWith { p1, p2 -> p1.rank.value.compareTo(p2.rank.value) }
}