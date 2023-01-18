package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.LoadingDialog
import ch.qscqlmpa.dwitch.ui.common.NavigationIcon
import ch.qscqlmpa.dwitch.ui.common.UiTags

@Preview
@Composable
fun HostNewGameScreenPreview() {
    PreviewContainer {
        HostNewGameBody(
            playerName = "Aragorn",
            gameName = "LOTR",
            hostGameControlEnabled = true,
            loading = false,
            {}, {}, {}, {}
        )
    }
}

@Composable
fun HostNewGameScreen(hostNewGameViewModel: HostNewGameViewModel) {
    DisposableEffect(key1 = hostNewGameViewModel) {
        hostNewGameViewModel.onStart()
        onDispose { hostNewGameViewModel.onStop() }
    }

    HostNewGameBody(
        playerName = hostNewGameViewModel.playerName.value,
        gameName = hostNewGameViewModel.gameName.value,
        hostGameControlEnabled = hostNewGameViewModel.canGameBeCreated.value,
        loading = hostNewGameViewModel.loading.value,
        onPlayerNameChange = hostNewGameViewModel::onPlayerNameChange,
        onGameNameChange = hostNewGameViewModel::onGameNameChange,
        onCreateGameClick = hostNewGameViewModel::hostGame,
        onBackClick = hostNewGameViewModel::onBackClick
    )
}

@Composable
fun HostNewGameBody(
    playerName: String,
    gameName: String,
    hostGameControlEnabled: Boolean,
    loading: Boolean,
    onPlayerNameChange: (String) -> Unit,
    onGameNameChange: (String) -> Unit,
    onCreateGameClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            DwitchTopBar(
                title = R.string.create_new_game,
                navigationIcon = NavigationIcon(
                    icon = R.drawable.ic_baseline_arrow_back_24,
                    contentDescription = R.string.back,
                    onClick = onBackClick
                )
            )
        }
    ) { innerPadding ->
        BackHandler(onBack = onBackClick)
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(8.dp)
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
        if (loading) LoadingDialog()
    }
}
