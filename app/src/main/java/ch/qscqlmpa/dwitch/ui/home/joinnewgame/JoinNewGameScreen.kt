package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.LoadingDialog
import ch.qscqlmpa.dwitch.ui.common.NavigationIcon
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun HostNewGameScreenPreview() {
    ActivityScreenContainer {
        JoinNewGameBody(
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
fun JoinNewGameScreen(
    vmFactory: ViewModelFactory,
    gameIpAddress: String,
    onJoinGameClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = viewModel<JoinNewGameViewModel>(factory = vmFactory)

    DisposableEffect(key1 = viewModel) {
        viewModel.onStart()
        onDispose { viewModel.onStop() }
    }

    Navigation(viewModel, onJoinGameClick)

    val game = remember { mutableStateOf(viewModel.getGame(gameIpAddress)) }
    JoinNewGameBody(
        gameName = game.value.gameName,
        playerName = viewModel.playerName.value,
        joinGameControlEnabled = viewModel.joinGameControl.value,
        loading = viewModel.loading.value,
        onPlayerNameChange = { name -> viewModel.onPlayerNameChange(name) },
        onJoinGameClick = { viewModel.joinGame() },
        onBackClick = onBackClick
    )
}

@Composable
fun JoinNewGameBody(
    gameName: String,
    playerName: String,
    joinGameControlEnabled: Boolean,
    loading: Boolean,
    onPlayerNameChange: (String) -> Unit,
    onJoinGameClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
private fun Navigation(
    viewModel: JoinNewGameViewModel,
    onJoinGameClick: () -> Unit
) {
    when (viewModel.navigation.value) {
        JoinNewGameDestination.CurrentScreen -> {
            // Nothing to do
        }
        JoinNewGameDestination.NavigateToWaitingRoom -> onJoinGameClick()
    }
}
