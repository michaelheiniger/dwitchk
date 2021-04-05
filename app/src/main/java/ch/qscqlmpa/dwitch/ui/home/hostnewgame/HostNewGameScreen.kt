package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun HostNewGameScreenPreview() {
    HostNewGameScreen("Aragorn", "LOTR", true, {}, {}, {}, {})
}

@Composable
fun HostNewGameScreen(
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
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        OutlinedTextField(
            value = playerName,
            label = { Text(stringResource(R.string.player_name)) },
            onValueChange = onPlayerNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("playerName")
        )
        OutlinedTextField(
            value = gameName,
            label = { Text(stringResource(R.string.nga_game_name_tv)) },
            onValueChange = onGameNameChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gameName")
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCreateGameClick,
            enabled = hostGameControlEnabled,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.host_game)) }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBackClick,
            Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.back_to_home_screen)) }
    }
}