package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ongoinggame.GameRulesDialog
import ch.qscqlmpa.dwitch.ui.ongoinggame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.DashboardScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround.EndOfRoundScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.GameRoomViewModel
import ch.qscqlmpa.dwitch.ui.theme.DwitchTheme
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState


@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun GameRoomHostScreenPreview() {
    DwitchTheme(false) {
        GameRoomHostBody(
            toolbarTitle = "Dwiiiitch",
            screen = null,
            connectionStatus = null,
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

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun GameRoomHostScreen(
    vmFactory: ViewModelFactory,
    onNavigationEvent: (GameRoomHostDestination) -> Unit
) {
    val playerViewModel = viewModel<GameRoomViewModel>(factory = vmFactory)
    val hostViewModel = viewModel<GameRoomHostViewModel>(factory = vmFactory)
    val connectionViewModel = viewModel<ConnectionHostViewModel>(factory = vmFactory)

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

    val event = hostViewModel.navigation.observeAsState().value
    if (event != null) onNavigationEvent(event)

    val toolbarTitle = playerViewModel.toolbarTitle.observeAsState(toolbarDefaultTitle).value
    val screen = playerViewModel.screen.observeAsState().value
    val connectionStatus = connectionViewModel.connectionStatus.observeAsState().value
    GameRoomHostBody(
        toolbarTitle = toolbarTitle,
        screen = screen,
        onCardClick = playerViewModel::onCardToPlayClick,
        onPlayClick = playerViewModel::onPlayClick,
        onPassClick = playerViewModel::onPassTurnClick,
        onCardToExchangeClick = playerViewModel::onCardToExchangeClick,
        onConfirmExchange = playerViewModel::confirmExchange,
        onStartNewRoundClick = hostViewModel::startNewRound,
        connectionStatus = connectionStatus,
        onEndGameConfirmClick = hostViewModel::endGame,
        onReconnectClick = connectionViewModel::reconnect
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun GameRoomHostBody(
    toolbarTitle: String,
    screen: GameRoomScreen?,
    connectionStatus: HostCommunicationState?,
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
    val showConfirmationDialog = remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(
                icon = R.drawable.ic_baseline_exit_to_app_24,
                contentDescription = R.string.end_game,
                onClick = { showConfirmationDialog.value = true }),
            actions = listOf(GameRules),
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
                is GameRoomScreen.Dashboard -> {
                    DashboardScreen(
                        dashboardInfo = screen.dashboardInfo,
                        onCardClick = onCardClick,
                        onPlayClick = onPlayClick,
                        onPassClick = onPassClick
                    )
                }
                is GameRoomScreen.EndOfRound -> {
                    EndOfRoundScreen(screen.endOfRoundInfo)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onStartNewRoundClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(UiTags.startNewRound)
                    ) { Text(stringResource(R.string.start_new_round)) }
                }
                is GameRoomScreen.CardExchange -> {
                    CardExchangeScreen(
                        numCardsToChoose = screen.cardExchangeState.numCardsToChoose,
                        cardsInHand = screen.cardExchangeState.cardsInHand,
                        canSubmitCardsForExchange = screen.cardExchangeState.canPerformExchange,
                        onCardClick = onCardToExchangeClick,
                        onConfirmExchangeClick = onConfirmExchange
                    )
                }
                is GameRoomScreen.CardExchangeOnGoing -> CardExchangeOnGoing()
                null -> LoadingSpinner()
            }
            ConnectionHostScreen(
                status = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = { showConfirmationDialog.value = true }
            )
        }

        if (showConfirmationDialog.value) {
            ConfirmationDialog(
                title = R.string.info_dialog_title,
                text = R.string.host_ends_game_confirmation,
                onConfirmClick = onEndGameConfirmClick,
                onCancelClick = { showConfirmationDialog.value = false }
            )
        }
    }
}
