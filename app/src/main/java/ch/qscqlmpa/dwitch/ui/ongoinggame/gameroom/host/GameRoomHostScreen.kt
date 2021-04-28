package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionHostScreen
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.NavigationIcon
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.ongoinggame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.DashboardScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround.EndOfRoundScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState

@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun GameRoomHostScreenPreview() {
    GameRoomHostScreen(
        toolbarTitle = "Dwiiiitch",
        screen = null,
        connectionStatus = null,
        onCardClick = {},
        onPassClick = {},
        onAddCardToExchange = {},
        onRemoveCardFromExchange = {},
        onConfirmExchange = {},
        onStartNewRoundClick = {},
        onEndGameClick = {},
        onReconnectClick = {}
    )
}

@ExperimentalFoundationApi
@Composable
fun GameRoomHostScreen(
    toolbarTitle: String,
    screen: GameRoomScreen?,
    connectionStatus: HostCommunicationState?,
    onCardClick: (Card) -> Unit,
    onPassClick: () -> Unit,
    onAddCardToExchange: (card: Card) -> Unit,
    onRemoveCardFromExchange: (card: Card) -> Unit,
    onConfirmExchange: () -> Unit,
    onStartNewRoundClick: () -> Unit,
    onEndGameClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(R.drawable.ic_baseline_exit_to_app_24, R.string.end_game, onEndGameClick)
        )

        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            when (screen) {
                is GameRoomScreen.Dashboard -> {
                    DashboardScreen(
                        dashboardInfo = screen.dashboardInfo,
                        onCardClick = onCardClick,
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
                        cardsToExchange = screen.cardExchangeState.cardsToExchange,
                        cardsInHand = screen.cardExchangeState.cardsInHand,
                        exchangeControlEnabled = screen.cardExchangeState.canPerformExchange,
                        onCardToExchangeClick = onRemoveCardFromExchange,
                        onCardInHandClick = onAddCardToExchange,
                        onConfirmExchangeClick = onConfirmExchange
                    )
                }
                is GameRoomScreen.CardExchangeOnGoing -> CardExchangeOnGoing()
                null -> LoadingSpinner()
            }
            ConnectionHostScreen(
                status = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = onEndGameClick
            )
        }
    }
}
