package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.model.UiControlModel


@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun HostNewGameScreenPreview() {
    JoinNewGameScreen("Aragorn", UiControlModel(), {}, {}, {})
}

@Composable
fun JoinNewGameScreen(
    playerName: String,
    joinGameBtnState: UiControlModel,
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
            label = { Text(stringResource(id = R.string.nga_player_name_tv)) },
            onValueChange = onPlayerNameChange,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onJoinGameClick,
            enabled = joinGameBtnState.enabled,
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(id = R.string.nga_join_game_btn)) }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBackClick,
            Modifier.fillMaxWidth()
        ) { Text(stringResource(id = R.string.nga_back_btn)) }
    }
}