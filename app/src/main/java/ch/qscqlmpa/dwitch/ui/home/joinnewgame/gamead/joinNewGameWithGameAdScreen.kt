package ch.qscqlmpa.dwitch.ui.home.joinnewgame.gamead

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.InfoDialog
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameBody
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameNotification
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchmodel.game.GameCommonId

@Composable
fun JoinNewGameWithGameAdScreen(
    vmFactory: ViewModelFactory,
    gameCommonId: GameCommonId
) {
    val viewModel = viewModel<JoinNewGameViewModel>(factory = vmFactory)

    DisposableEffect(key1 = viewModel) {
        viewModel.onStart()
        viewModel.loadGame(gameCommonId)
        onDispose { viewModel.onStop() }
    }

    Notification(
        notification = viewModel.notification.value,
        onGameNotFoundAcknowledge = viewModel::onGameNotFoundAcknowledge
    )

    JoinNewGameBody(
        gameName = viewModel.gameName.value,
        playerName = viewModel.playerName.value,
        joinGameControlEnabled = viewModel.canJoinGame.value,
        loading = viewModel.loading.value,
        onPlayerNameChange = viewModel::onPlayerNameChange,
        onJoinGameClick = { viewModel.joinGame() },
        onBackClick = viewModel::onBackClick
    )
}

@Composable
private fun Notification(
    notification: JoinNewGameNotification,
    onGameNotFoundAcknowledge: () -> Unit
) {
    when (notification) {
        JoinNewGameNotification.None -> {
            // Nothing to do
        }
        JoinNewGameNotification.GameNotFound -> {
            InfoDialog(
                title = R.string.game_not_found_title,
                text = R.string.game_not_found_text,
                onOkClick = onGameNotFoundAcknowledge
            )
        }
        JoinNewGameNotification.ErrorJoiningGame -> {
            InfoDialog(
                title = R.string.dialog_error_title,
                text = R.string.error_joining_game_text,
                onOkClick = {} // Nothing to do
            )
        }
    }
}
