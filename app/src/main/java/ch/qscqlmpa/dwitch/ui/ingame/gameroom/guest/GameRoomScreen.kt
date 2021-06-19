package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.DashboardInfo
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchangeState
import ch.qscqlmpa.dwitchgame.ingame.gameroom.EndOfRoundInfo

sealed class GameRoomScreen {
    data class Dashboard(val dashboardInfo: DashboardInfo) : GameRoomScreen()
    data class EndOfRound(val endOfRoundInfo: EndOfRoundInfo) : GameRoomScreen()
    data class CardExchange(val cardExchangeState: CardExchangeState) : GameRoomScreen()
    object CardExchangeOnGoing : GameRoomScreen()
}
