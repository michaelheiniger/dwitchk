package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameDashboardInfo

sealed class GameRoomScreen {
    data class Dashboard(val dashboardInfo: GameDashboardInfo) : GameRoomScreen()
    data class EndOfRound(val endOfRoundInfo: EndOfRoundInfo) : GameRoomScreen()
    data class CardExchange(val cardExchangeState: CardExchangeState) : GameRoomScreen()
    object CardExchangeOnGoing : GameRoomScreen()
}
