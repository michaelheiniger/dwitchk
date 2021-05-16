package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionGuestScreen
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.NavigationIcon
import ch.qscqlmpa.dwitch.ui.ongoinggame.GameOverDialog
import ch.qscqlmpa.dwitch.ui.ongoinggame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.DashboardScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround.EndOfRoundScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun GameRoomHostScreenPreview() {
    GameRoomGuestScreen(
        toolbarTitle = "Dwiiiitch",
        screen = null,
        connectionStatus = null,
        showGameOverDialog = false,
        onGameOverAcknowledge = {},
        onCardClick = {},
        onPlayClick = {},
        onPassClick = {},
        onAddCardToExchange = {},
        onConfirmExchange = {},
        onReconnectClick = {},
        onLeaveGameClick = {}
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun GameRoomGuestScreen(
    toolbarTitle: String,
    screen: GameRoomScreen?,
    connectionStatus: GuestCommunicationState?,
    showGameOverDialog: Boolean,
    onGameOverAcknowledge: () -> Unit,
    onCardClick: (Card) -> Unit,
    onPlayClick: () -> Unit,
    onPassClick: () -> Unit,
    onAddCardToExchange: (card: Card) -> Unit,
    onConfirmExchange: () -> Unit,
    onReconnectClick: () -> Unit,
    onLeaveGameClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(R.drawable.ic_baseline_exit_to_app_24, R.string.leave_game, onLeaveGameClick)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            if (showGameOverDialog) {
                GameOverDialog(onGameOverAcknowledge)
            }

            when (screen) {
                is GameRoomScreen.Dashboard -> DashboardScreen(
                    dashboardInfo = screen.dashboardInfo,
                    onCardClick = onCardClick,
                    onPlayClick = onPlayClick,
                    onPassClick = onPassClick
                )
                is GameRoomScreen.EndOfRound -> EndOfRoundScreen(screen.endOfRoundInfo)
                is GameRoomScreen.CardExchange -> {
                    CardExchangeScreen(
                        numCardsToChoose = screen.cardExchangeState.numCardsToChoose,
                        cardsInHand = screen.cardExchangeState.cardsInHand,
                        canSubmitCardsForExchange = screen.cardExchangeState.canPerformExchange,
                        onCardClick = onAddCardToExchange,
                        onConfirmExchangeClick = onConfirmExchange
                    )
                }
                is GameRoomScreen.CardExchangeOnGoing -> CardExchangeOnGoing()
                null -> LoadingSpinner()
            }

            Spacer(Modifier.height(16.dp))
            ConnectionGuestScreen(
                status = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = onLeaveGameClick
            )
        }
    }
}
