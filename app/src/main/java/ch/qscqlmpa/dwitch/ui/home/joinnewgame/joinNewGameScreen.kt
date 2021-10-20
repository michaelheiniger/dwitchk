package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo

@Composable
fun JoinNewGameScreen(
    joinNewGameViewModel: JoinNewGameViewModel,
    gameAd: GameAdvertisingInfo
) {
    DisposableEffect(key1 = joinNewGameViewModel) {
        joinNewGameViewModel.onStart()
        joinNewGameViewModel.loadGame(gameAd)
        onDispose { joinNewGameViewModel.onStop() }
    }

    JoinNewGameBody(
        notification = joinNewGameViewModel.notification.value,
        gameName = joinNewGameViewModel.gameName.value,
        playerName = joinNewGameViewModel.playerName.value,
        joinGameControlEnabled = joinNewGameViewModel.canJoinGame.value,
        loading = joinNewGameViewModel.loading.value,
        onPlayerNameChange = joinNewGameViewModel::onPlayerNameChange,
        onJoinGameClick = { joinNewGameViewModel.joinGame() },
        onBackClick = joinNewGameViewModel::onBackClick
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun JoinNewGameBodyPreview() {
    ActivityScreenContainer {
        JoinNewGameBody(
            notification = JoinNewGameNotification.None,
            gameName = "Dwiiitch",
            playerName = "Aragorn",
            joinGameControlEnabled = false,
            loading = false,
            onPlayerNameChange = {},
            onJoinGameClick = {},
            onBackClick = {}
        )
    }
}

@Composable
fun JoinNewGameBody(
    notification: JoinNewGameNotification,
    gameName: String,
    playerName: String,
    joinGameControlEnabled: Boolean,
    loading: Boolean,
    onPlayerNameChange: (String) -> Unit,
    onJoinGameClick: () -> Unit,
    onBackClick: () -> Unit
) {
    BackHandler(onBack = onBackClick)
    Notification(notification = notification)
    if (loading) LoadingDialog()
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = gameName,
            navigationIcon = NavigationIcon(
                icon = R.drawable.ic_baseline_arrow_back_24,
                contentDescription = R.string.back,
                onClick = onBackClick
            ),
            actions = emptyList(),
            onActionClick = {}
        )
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = playerName,
                label = { Text(stringResource(R.string.player_name)) },
                onValueChange = onPlayerNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(UiTags.playerName)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onJoinGameClick,
                enabled = joinGameControlEnabled,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.join_game)) }
        }
    }
}

@Composable
private fun Notification(notification: JoinNewGameNotification) {
    when (notification) {
        JoinNewGameNotification.None -> {
            // Nothing to do
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
