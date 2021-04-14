package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import DashboardScreen
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.ui.ConnectionGuestScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.GameOverDialog
import ch.qscqlmpa.dwitch.ui.ongoinggame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeOnGoing
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround.EndOfRoundScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState

@ExperimentalFoundationApi
@Composable
fun GameRoomGuestScreen(
    screen: GameRoomScreen?,
    showGameOver: Boolean,
    onGameOverAcknowledge: () -> Unit,
    onCardClick: (Card) -> Unit,
    onPassClick: () -> Unit,
    onAddCardToExchange: (card: Card) -> Unit,
    onRemoveCardFromExchange: (card: Card) -> Unit,
    onConfirmExchange: () -> Unit,
    connectionStatus: GuestCommunicationState?,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        if (showGameOver) {
            GameOverDialog(onGameOverAcknowledge)
        }

        when (screen) {
            is GameRoomScreen.Dashboard -> DashboardScreen(
                dashboardInfo = screen.dashboardInfo,
                onCardClick = onCardClick,
                onPassClick = onPassClick
            )
            is GameRoomScreen.EndOfRound -> EndOfRoundScreen(screen.endOfRoundInfo)
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

        Spacer(Modifier.height(16.dp))
        ConnectionGuestScreen(connectionStatus) { onReconnectClick() }
    }
}

