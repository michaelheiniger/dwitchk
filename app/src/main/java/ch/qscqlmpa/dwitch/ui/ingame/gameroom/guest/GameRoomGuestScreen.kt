package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ingame.GameOverDialog
import ch.qscqlmpa.dwitch.ui.ingame.GameRulesDialog
import ch.qscqlmpa.dwitch.ui.ingame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ingame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.Dashboard
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchange
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.endofround.EndOfRound
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState

@Preview
@Composable
fun GameRoomGuestBodyPreview() {
    PreviewContainer {
        GameRoomGuestBody(
            toolbarTitle = "Dwiitch",
            screen = GameRoomScreen.CardExchangeOnGoing,
            connectionStatus = GuestCommunicationState.Connected,
            gameOver = false,
            leavingGame = false,
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

@Composable
fun GameRoomGuestScreen(
    playerViewModel: GameRoomViewModel,
    guestViewModel: GameRoomGuestViewModel,
    connectionViewModel: ConnectionGuestViewModel
) {
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

    GameRoomGuestBody(
        toolbarTitle = playerViewModel.toolbarTitle.value,
        screen = playerViewModel.screen.value,
        connectionStatus = connectionViewModel.connectionState.value,
        gameOver = guestViewModel.gameOver.value,
        leavingGame = guestViewModel.leavingGame.value,
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

@Composable
fun GameRoomGuestBody(
    toolbarTitle: String,
    screen: GameRoomScreen?,
    connectionStatus: GuestCommunicationState,
    gameOver: Boolean,
    leavingGame: Boolean,
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
    Scaffold(
        topBar = {
            DwitchTopBar(
                title = toolbarTitle,
                navigationIcon = NavigationIcon(
                    icon = R.drawable.ic_baseline_exit_to_app_24,
                    contentDescription = R.string.leave_game,
                    onClick = { showLeaveGameConfirmationDialog.value = true }
                ),
                actions = listOf(GameRules),
                onActionClick = { action ->
                    when (action) {
                        GameRules -> gameRules.value = true
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(8.dp)
        ) {
            when (screen) {
                is GameRoomScreen.Dashboard -> Dashboard(
                    dashboardInfo = screen.dashboardInfo,
                    onCardClick = onCardClick,
                    onPlayClick = onPlayClick,
                    onPassClick = onPassClick,
                    onEndOrLeaveGameClick = { showLeaveGameConfirmationDialog.value = true }
                )
                is GameRoomScreen.EndOfRound -> EndOfRound(screen.endOfRoundInfo)
                is GameRoomScreen.CardExchange -> {
                    CardExchange(
                        numCardsToChoose = screen.cardExchangeState.numCardsToChoose,
                        cardsInHand = screen.cardExchangeState.cardsInHand,
                        canSubmitCardsForExchange = screen.cardExchangeState.canPerformExchange,
                        onCardClick = onAddCardToExchange,
                        onConfirmExchangeClick = onConfirmExchange
                    )
                }
                is GameRoomScreen.CardExchangeOnGoing -> CardExchangeOnGoing()
                else -> LoadingSpinner()
            }
        }

        if (gameRules.value) GameRulesDialog(onOkClick = { gameRules.value = false })

        when {
            leavingGame -> LoadingDialog(R.string.leaving_game)
            gameOver -> GameOverDialog(onGameOverAcknowledge = onGameOverAcknowledge)
            else -> CommunicationGuest(
                state = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = { showLeaveGameConfirmationDialog.value = true }
            )
        }

        if (showLeaveGameConfirmationDialog.value) {
            ConfirmationDialog(
                title = R.string.dialog_info_title,
                text = R.string.guest_leaves_game_confirmation,
                onConfirmClick = onLeaveGameConfirmClick,
                onClosing = { showLeaveGameConfirmationDialog.value = false }
            )
        }
    }
}
