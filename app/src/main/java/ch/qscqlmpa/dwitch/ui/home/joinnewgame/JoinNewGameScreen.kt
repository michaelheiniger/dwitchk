package ch.qscqlmpa.dwitch.ui.home.joinnewgame

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
import ch.qscqlmpa.dwitch.ui.common.UiTags

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun HostNewGameScreenPreview() {
    JoinNewGameScreen("Aragorn", true, {}, {}, {})
}

@Composable
fun JoinNewGameScreen(
    playerName: String,
    joinGameControlEnabled: Boolean,
    onPlayerNameChange: (String) -> Unit,
    onJoinGameClick: () -> Unit,
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
                .testTag(UiTags.playerName)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onJoinGameClick,
            enabled = joinGameControlEnabled,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.join_game)) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onBackClick,
            Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.back_to_home_screen)) }
    }
}
