package ch.qscqlmpa.dwitch.ui.ongoinggame.endofround

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.ongoinggame.game.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerEndOfRoundInfo

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun EndOfRoundInfoDialogLoadingScreenPreview() {
    EndOfRoundScreen(endOfRoundInfo = null, onStartNewRoundClick = {}, onGameOverClick = {}, onLeaveGameClick = {})
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun EndOfRoundInfoDialogPreview() {
    val endOfRoundInfo = EndOfRoundInfo(
        listOf(
            PlayerEndOfRoundInfo("Legolas", Rank.President),
            PlayerEndOfRoundInfo("Gimli", Rank.VicePresident),
            PlayerEndOfRoundInfo("Gollum", Rank.Neutral),
            PlayerEndOfRoundInfo("Merry", Rank.ViceAsshole),
            PlayerEndOfRoundInfo("Pipin", Rank.Asshole),
        ),
        canStartNewRound = true,
        canEndGame = true
    )
    EndOfRoundScreen(endOfRoundInfo, onStartNewRoundClick = {}, onGameOverClick = {}, onLeaveGameClick = {})
}

@Composable
fun EndOfRoundScreen(
    endOfRoundInfo: EndOfRoundInfo?,
    onStartNewRoundClick: () -> Unit,
    onGameOverClick: () -> Unit,
    onLeaveGameClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        if (endOfRoundInfo == null) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Text(
                    text = stringResource(R.string.end_of_round_screen_title),
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                items(endOfRoundInfo.playersInfo) { info ->
                    Text(text = "${info.name}: ${stringResource(ResourceMapper.getResourceLong(info.rank))}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (endOfRoundInfo.canStartNewRound) {
                    Button(onClick = onStartNewRoundClick) {
                        Text(stringResource(id = R.string.pdf_start_new_round))
                    }
                }
                if (endOfRoundInfo.canEndGame) {
                    Button(onClick = onGameOverClick) {
                        Text(stringResource(id = R.string.grhf_end_game_btn))
                    }
                } else {
                    Button(onClick = onLeaveGameClick) {
                        Text(stringResource(id = R.string.leave_game_btn))
                    }
                }
            }
        }
    }
}