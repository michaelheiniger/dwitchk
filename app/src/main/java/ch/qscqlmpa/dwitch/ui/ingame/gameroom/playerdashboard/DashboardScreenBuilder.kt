package ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.DashboardInfo
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.LocalPlayerInfo
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.DwitchCardInfoValueDescComparator
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameDashboardInfo
import org.tinylog.kotlin.Logger

class DashboardScreenBuilder constructor(
    gameDashboardInfo: GameDashboardInfo
) : GameRoomScreenBuilder {
    private var dashboardInfo: DashboardInfo
    private val playCardEngine: PlayCardEngine

    val selectedCards get() = playCardEngine.getSelectedCards()
    override val screen get() = GameRoomScreen.Dashboard(dashboardInfo)

    init {
        Logger.debug { "Create new ScreenBuilder ($this)" }
        playCardEngine = PlayCardEngine(
            sortCardsInHand(gameDashboardInfo),
            gameDashboardInfo.lastCardPlayed
        )
        dashboardInfo = DashboardInfo(
            playersInfo = gameDashboardInfo.playersInfo,
            localPlayerInfo = LocalPlayerInfo(
                cardsInHand = playCardEngine.getCardsInHand(),
                canPlay = playCardEngine.cardSelectionIsValid(),
                canPass = gameDashboardInfo.localPlayerDashboard.canPass
            ),
            lastPlayerAction = gameDashboardInfo.lastPlayerAction,
            lastCardOnTable = gameDashboardInfo.lastCardPlayed,
            waitingForPlayerReconnection = gameDashboardInfo.waitingForPlayerReconnection
        )
    }

    fun onCardClick(cardPlayed: Card): GameRoomScreen.Dashboard {
        Logger.debug { "Click on card $cardPlayed" }
        playCardEngine.onCardClick(cardPlayed)
        val localPlayerInfo = dashboardInfo.localPlayerInfo.copy(
            cardsInHand = playCardEngine.getCardsInHand(),
            canPlay = playCardEngine.cardSelectionIsValid()
        )
        dashboardInfo = dashboardInfo.copy(localPlayerInfo = localPlayerInfo)
        return GameRoomScreen.Dashboard(dashboardInfo)
    }

    private fun sortCardsInHand(gameDashboardInfo: GameDashboardInfo) =
        gameDashboardInfo.localPlayerDashboard.cardsInHand
            .sortedWith(DwitchCardInfoValueDescComparator())
            .map { c -> CardInfo(c.card, c.selectable) }
}
