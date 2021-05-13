package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.DashboardInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.EndOfRoundInfo

sealed class GameRoomScreen {
    data class Dashboard(val dashboardInfo: DashboardInfo) : GameRoomScreen()
    data class EndOfRound(val endOfRoundInfo: EndOfRoundInfo) : GameRoomScreen()
    data class CardExchange(val cardExchangeState: CardExchangeState) : GameRoomScreen()
    object CardExchangeOnGoing : GameRoomScreen()
}
