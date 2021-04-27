package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionHostScreen
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
@Composable
fun GameRoomHostScreen(
    screen: GameRoomScreen?,
    onCardClick: (Card) -> Unit,
    onPassClick: () -> Unit,
    onAddCardToExchange: (card: Card) -> Unit,
    onRemoveCardFromExchange: (card: Card) -> Unit,
    onConfirmExchange: () -> Unit,
    onStartNewRoundClick: () -> Unit,
    connectionStatus: HostCommunicationState?,
    onEndGameClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
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
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onEndGameClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.endGame)
        ) {
            Text(stringResource(R.string.end_game))
        }
        ConnectionHostScreen(
            status = connectionStatus,
            onReconnectClick = onReconnectClick,
            onAbortClick = onEndGameClick
        )
    }
}
