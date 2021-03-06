package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.BuildConfig
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
        HostNewGameBody("Aragorn", "LOTR", true, {}, {}, {}, {})
    }
}

@Composable
fun HostNewGameScreen(
    vmFactory: ViewModelFactory,
    onHostGameClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = viewModel<HostNewGameViewModel>(factory = vmFactory)

    DisposableEffect(key1 = viewModel) {
        viewModel.onStart()
        onDispose { viewModel.onStop() }
    }

    when (viewModel.navigation.observeAsState().value) {
        HostNewGameDestination.NavigateToWaitingRoom -> onHostGameClick()
    }

    val initialPlayerName = if (BuildConfig.DEBUG) "Mirlick" else ""
    val initialGameName = if (BuildConfig.DEBUG) "Dwiiitch !" else ""
    val initialHostGameControl = BuildConfig.DEBUG
    val playerName = viewModel.playerName.observeAsState(initialPlayerName).value
    val gameName = viewModel.gameName.observeAsState(initialGameName).value
    val hostGameControl = viewModel.createGameControl.observeAsState(initialHostGameControl).value
    HostNewGameBody(
        playerName = playerName,
        gameName = gameName,
        hostGameControlEnabled = hostGameControl,
        onPlayerNameChange = { name -> viewModel.onPlayerNameChange(name) },
        onGameNameChange = { name -> viewModel.onGameNameChange(name) },
        onCreateGameClick = { viewModel.hostGame() },
        onBackClick = onBackClick
    )
    if (viewModel.loading.observeAsState(false).value) LoadingDialog()
}


@Composable
fun HostNewGameBody(
    playerName: String,
    gameName: String,
    hostGameControlEnabled: Boolean,
    onPlayerNameChange: (String) -> Unit,
    onGameNameChange: (String) -> Unit,
    onCreateGameClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = stringResource(R.string.create_new_game),
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
            OutlinedTextField(
                value = gameName,
                label = { Text(stringResource(R.string.nga_game_name_tv)) },
                onValueChange = onGameNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(UiTags.gameName)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onCreateGameClick,
                enabled = hostGameControlEnabled,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.host_game)) }
        }
    }
}
