package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ingame.GameOverDialog
import ch.qscqlmpa.dwitch.ui.ingame.GameRulesDialog
import ch.qscqlmpa.dwitch.ui.ingame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ingame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.DashboardScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchangeScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.endofround.EndOfRoundScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun GameRoomHostScreenPreview() {
    ActivityScreenContainer {
        GameRoomGuestBody(
            toolbarTitle = "Dwiitch",
            screen = GameRoomScreen.CardExchangeOnGoing,
            connectionStatus = GuestCommunicationState.Connected,
            showGameOver = false,
            onCardClick = {},
            onPlayClick = {},
            onPassClick = {},
            onAddCardToExchange = {},
            onConfirmExchange = {},
            onLeaveGameConfirmClick = {},
            onGameOverAcknowledge = {},
            onReconnectClick = {}
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun GameRoomGuestScreen(
    vmFactory: ViewModelFactory,
    onNavigationEvent: (GameRoomGuestDestination) -> Unit
) {
    val playerViewModel = viewModel<GameRoomViewModel>(factory = vmFactory)
    val guestViewModel = viewModel<GameRoomGuestViewModel>(factory = vmFactory)
    val connectionViewModel = viewModel<ConnectionGuestViewModel>(factory = vmFactory)

    DisposableEffect(playerViewModel, guestViewModel, connectionViewModel) {
        playerViewModel.onStart()
        guestViewModel.onStart()
        connectionViewModel.onStart()
        onDispose {
            playerViewModel.onStop()
            guestViewModel.onStop()
            connectionViewModel.onStop()
        }
    }

    val event = guestViewModel.navigation.observeAsState().value
    if (event != null) onNavigationEvent(event)

    val toolbarTitle = playerViewModel.toolbarTitle.observeAsState(toolbarDefaultTitle).value
    val screen = playerViewModel.screen.observeAsState().value
    val connectionStatus = connectionViewModel.connectionStatus.observeAsState().value
    val gameOver = guestViewModel.gameOver.observeAsState(false).value

    GameRoomGuestBody(
        toolbarTitle = toolbarTitle,
        screen = screen,
        connectionStatus = connectionStatus,
        showGameOver = gameOver,
        onCardClick = playerViewModel::onCardToPlayClick,
        onPlayClick = playerViewModel::onPlayClick,
        onPassClick = playerViewModel::onPassTurnClick,
        onAddCardToExchange = playerViewModel::onCardToExchangeClick,
        onConfirmExchange = playerViewModel::confirmExchange,
        onLeaveGameConfirmClick = guestViewModel::leaveGame,
        onGameOverAcknowledge = guestViewModel::acknowledgeGameOver,
        onReconnectClick = connectionViewModel::reconnect
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun GameRoomGuestBody(
    toolbarTitle: String,
    screen: GameRoomScreen?,
    connectionStatus: GuestCommunicationState?,
    showGameOver: Boolean,
    onCardClick: (Card) -> Unit,
    onPlayClick: () -> Unit,
    onPassClick: () -> Unit,
    onAddCardToExchange: (card: Card) -> Unit,
    onConfirmExchange: () -> Unit,
    onLeaveGameConfirmClick: () -> Unit,
    onGameOverAcknowledge: () -> Unit,
    onReconnectClick: () -> Unit,
) {
    val gameRules = remember { mutableStateOf(false) }
    val showLeaveGameConfirmationDialog = remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(
                icon = R.drawable.ic_baseline_exit_to_app_24,
                contentDescription = R.string.leave_game,
                onClick = { showLeaveGameConfirmationDialog.value = true }
            ),
            actions = emptyList(),
            onActionClick = { action ->
                when (action) {
                    GameRules -> gameRules.value = true
                }
            }
        )

        if (gameRules.value) GameRulesDialog(onOkClick = { gameRules.value = false })

        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            when (screen) {
                is GameRoomScreen.Dashboard -> DashboardScreen(
                    dashboardInfo = screen.dashboardInfo,
                    onCardClick = onCardClick,
                    onPlayClick = onPlayClick,
                    onPassClick = onPassClick,
                    onEndOrLeaveGameClick = { showLeaveGameConfirmationDialog.value = true }
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
        }
    }

    if (showGameOver) {
        GameOverDialog(onGameOverAcknowledge = onGameOverAcknowledge)
    } else {
        ConnectionGuestScreen(
            status = connectionStatus,
            onReconnectClick = onReconnectClick,
            onAbortClick = { showLeaveGameConfirmationDialog.value = true }
        )
    }

    if (showLeaveGameConfirmationDialog.value) {
        ConfirmationDialog(
            title = R.string.info_dialog_title,
            text = R.string.guest_leaves_game_confirmation,
            onConfirmClick = onLeaveGameConfirmClick,
            onCancelClick = { showLeaveGameConfirmationDialog.value = false }
        )
    }
}
