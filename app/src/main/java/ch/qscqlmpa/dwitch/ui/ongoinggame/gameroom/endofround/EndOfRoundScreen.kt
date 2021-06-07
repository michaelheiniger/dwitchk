package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerEndOfRoundInfo

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun EndOfRoundInfoDialogPreview() {
    val endOfRoundInfo = EndOfRoundInfo(
        listOf(
            PlayerEndOfRoundInfo("Legolas", DwitchRank.President),
            PlayerEndOfRoundInfo("Gimli", DwitchRank.VicePresident),
            PlayerEndOfRoundInfo("Gollum", DwitchRank.Neutral),
            PlayerEndOfRoundInfo("Merry", DwitchRank.ViceAsshole),
            PlayerEndOfRoundInfo("Pipin", DwitchRank.Asshole),
        ),
        canStartNewRound = true,
        canEndGame = true
    )
    ActivityScreenContainer {
        EndOfRoundScreen(endOfRoundInfo)
    }
}

@Composable
fun EndOfRoundScreen(endOfRoundInfo: EndOfRoundInfo) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
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

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            items(endOfRoundInfo.playersInfo) { player ->
                Text(
                    text = "${player.name}: ${stringResource(ResourceMapper.getResourceLong(player.rank))}",
                    modifier = Modifier.testTag(player.name)
                )
            }
        }
    }
}
