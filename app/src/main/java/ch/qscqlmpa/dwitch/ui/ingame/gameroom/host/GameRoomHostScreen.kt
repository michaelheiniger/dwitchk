package ch.qscqlmpa.dwitch.ui.ingame.gameroom.host

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ingame.GameRulesDialog
import ch.qscqlmpa.dwitch.ui.ingame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ingame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.Dashboard
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchange
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.endofround.EndOfRound
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState

@Preview
@Composable
fun GameRoomHostBodyPreview() {
    DwitchTheme() {
        GameRoomHostBody(
            toolbarTitle = "Dwiiiitch",
            screen = null,
            connectionStatus = HostCommunicationState.Online,
            endingGame = false,
            onCardClick = {},
            onPlayClick = {},
            onPassClick = {},
            onCardToExchangeClick = {},
            onConfirmExchange = {},
            onStartNewRoundClick = {},
            onEndGameConfirmClick = {},
            onReconnectClick = {}
        )
    }
}

@Composable
fun GameRoomHostScreen(
    playerViewModel: GameRoomViewModel,
    hostViewModel: GameRoomHostViewModel,
    connectionViewModel: ConnectionHostViewModel
) {
    DisposableEffect(playerViewModel, hostViewModel, connectionViewModel) {
        playerViewModel.onStart()
        hostViewModel.onStart()
        connectionViewModel.onStart()
        onDispose {
            playerViewModel.onStop()
            hostViewModel.onStop()
            connectionViewModel.onStop()
        }
    }

    GameRoomHostBody(
        toolbarTitle = playerViewModel.toolbarTitle.value,
        screen = playerViewModel.screen.value,
        endingGame = hostViewModel.endingGame.value,
        onCardClick = playerViewModel::onCardToPlayClick,
        onPlayClick = playerViewModel::onPlayClick,
        onPassClick = playerViewModel::onPassTurnClick,
        onCardToExchangeClick = playerViewModel::onCardToExchangeClick,
        onConfirmExchange = playerViewModel::confirmExchange,
        onStartNewRoundClick = hostViewModel::startNewRound,
        connectionStatus = connectionViewModel.connectionStatus.value,
        onEndGameConfirmClick = hostViewModel::endGame,
        onReconnectClick = connectionViewModel::reconnect
    )
}

@Composable
fun GameRoomHostBody(
    toolbarTitle: String,
    screen: GameRoomScreen?,
    connectionStatus: HostCommunicationState,
    endingGame: Boolean,
    onCardClick: (Card) -> Unit,
    onPlayClick: () -> Unit,
    onPassClick: () -> Unit,
    onCardToExchangeClick: (card: Card) -> Unit,
    onConfirmExchange: () -> Unit,
    onStartNewRoundClick: () -> Unit,
    onEndGameConfirmClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
    val gameRules = remember { mutableStateOf(false) }
    val showEndGameConfirmationDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            DwitchTopBar(
                title = toolbarTitle,
                navigationIcon = NavigationIcon(
                    icon = R.drawable.ic_baseline_exit_to_app_24,
                    contentDescription = R.string.end_game,
                    onClick = { showEndGameConfirmationDialog.value = true }
                ),
                actions = listOf(GameRules),
                onActionClick = { gameRules.value = true }
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
                is GameRoomScreen.Dashboard -> {
                    Dashboard(
                        dashboardInfo = screen.dashboardInfo,
                        onCardClick = onCardClick,
                        onPlayClick = onPlayClick,
                        onPassClick = onPassClick,
                        onEndOrLeaveGameClick = { showEndGameConfirmationDialog.value = true }
                    )
                }
                is GameRoomScreen.EndOfRound -> {
                    EndOfRound(screen.endOfRoundInfo)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onStartNewRoundClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(UiTags.startNewRound)
                    ) { Text(stringResource(R.string.start_new_round)) }
                }
                is GameRoomScreen.CardExchange -> {
                    CardExchange(
                        numCardsToChoose = screen.cardExchangeState.numCardsToChoose,
                        cardsInHand = screen.cardExchangeState.cardsInHand,
                        canSubmitCardsForExchange = screen.cardExchangeState.canPerformExchange,
                        onCardClick = onCardToExchangeClick,
                        onConfirmExchangeClick = onConfirmExchange
                    )
                }
                is GameRoomScreen.CardExchangeOnGoing -> CardExchangeOnGoing()
                else -> LoadingSpinner()
            }
            if (gameRules.value) GameRulesDialog(onOkClick = { gameRules.value = false })
            when {
                endingGame -> LoadingDialog(R.string.ending_game)
                else -> CommunicationHost(
                    state = connectionStatus,
                    onReconnectClick = onReconnectClick,
                    onAbortClick = { showEndGameConfirmationDialog.value = true }
                )
            }

            if (showEndGameConfirmationDialog.value) {
                ConfirmationDialog(
                    title = R.string.dialog_info_title,
                    text = R.string.host_ends_game_confirmation,
                    onConfirmClick = onEndGameConfirmClick,
                    onClosing = { showEndGameConfirmationDialog.value = false }
                )
            }
        }
    }
}
