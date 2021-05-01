package ch.qscqlmpa.dwitch.ui.home.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.common.toStringEuFormat
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import org.joda.time.DateTime

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun HomeScreenPreview() {
    val advertisedGame = LoadedData.Success(
        listOf(
            AdvertisedGame(false, "Game 1", GameCommonId(1), "192.168.1.1", 8889),
            AdvertisedGame(false, "Game 2", GameCommonId(2), "192.168.1.2", 8889),
            AdvertisedGame(false, "Game 3", GameCommonId(3), "192.168.1.3", 8889)
        )
    )

    val resumableGameResponse = LoadedData.Success(
        listOf(
            ResumableGameInfo(1, DateTime.parse("2020-07-26T01:20+02:00"), "LOTR", listOf("Aragorn", "Legolas", "Gimli")),
            ResumableGameInfo(
                2,
                DateTime.parse("2021-01-01T01:18+02:00"),
                "GoT",
                listOf("Ned Stark", "Arya Stark", "Sandor Clegane")
            )
        )
    )
    HomeScreen(
        advertisedGames = advertisedGame,
        resumableGames = resumableGameResponse,
        onCreateNewGameClick = {},
        onJoinGameClick = {},
        onResumableGameClick = {}
    )
}

@Composable
fun HomeScreen(
    advertisedGames: LoadedData<List<AdvertisedGame>>,
    resumableGames: LoadedData<List<ResumableGameInfo>>,
    onCreateNewGameClick: () -> Unit,
    onJoinGameClick: (AdvertisedGame) -> Unit,
    onResumableGameClick: (ResumableGameInfo) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(title = R.string.app_name)
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)

        ) {
            AdvertisedGameContainer(advertisedGames, onJoinGameClick)
            Spacer(Modifier.height(16.dp))
            GameCreation(onCreateNewGameClick)
            Spacer(Modifier.height(16.dp))
            ResumableGamesContainer(resumableGames, onResumableGameClick)
        }
    }
}

@Composable
private fun GameCreation(onCreateNewGameClick: () -> Unit) {
    Button(
        onClick = onCreateNewGameClick,
        modifier = Modifier.fillMaxWidth()
    ) { Text(stringResource(R.string.create_game)) }
}

@Composable
private fun AdvertisedGameContainer(
    advertisedGames: LoadedData<List<AdvertisedGame>>,
    onJoinGameClick: (AdvertisedGame) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Text(
            text = stringResource(R.string.advertised_games),
            fontSize = 30.sp,
            color = MaterialTheme.colors.primary
        )
        when (advertisedGames) {
            LoadedData.Loading -> ListeningForAdvertisedGames()
            is LoadedData.Success -> AdvertisedGames(advertisedGames.data, onJoinGameClick)
            is LoadedData.Failed -> Text(stringResource(R.string.listening_advertised_games_failed), color = Color.Red)
        }
    }
}

@Composable
private fun AdvertisedGames(
    advertisedGames: List<AdvertisedGame>,
    onJoinGameClick: (AdvertisedGame) -> Unit
) {
    if (advertisedGames.isEmpty()) {
        ListeningForAdvertisedGames()
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(advertisedGames) { game ->
                val text = if (BuildConfig.DEBUG) "${game.gameName} (${game.gameIpAddress})" else game.gameName
                val contentDescription = stringResource(R.string.join_specific_game_cd, game.gameName)
                Text(
                    text = text,
                    Modifier
                        .clickable { onJoinGameClick(game) }
                        .semantics { this.contentDescription = contentDescription }
                )
            }
        }
    }
}

@Composable
private fun ListeningForAdvertisedGames() {
    Text(stringResource(R.string.no_game_discovered))
}

@Composable
fun ResumableGamesContainer(
    resumableGames: LoadedData<List<ResumableGameInfo>>,
    onResumableGameClick: (ResumableGameInfo) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()

    ) {
        Text(
            stringResource(R.string.resumable_games),
            fontSize = 32.sp,
            color = MaterialTheme.colors.primary
        )
        when (resumableGames) {
            LoadedData.Loading -> Text(stringResource(R.string.loading_resumable_games))
            is LoadedData.Success -> ResumableGames(resumableGames.data, onResumableGameClick)
            is LoadedData.Failed -> Text(stringResource(R.string.loading_resumable_games_failed), color = Color.Red)
        }
    }
}

@Composable
private fun ResumableGames(
    resumableGames: List<ResumableGameInfo>,
    onResumableGameclick: (ResumableGameInfo) -> Unit
) {
    if (resumableGames.isEmpty()) {
        Text(stringResource(R.string.no_resumable_games))
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(resumableGames) { game ->
                Text(
                    text = "${game.name} (${game.creationDate.toStringEuFormat()} - ${game.playersName.joinToString(", ")})",
                    Modifier.clickable { onResumableGameclick(game) }
                )
            }
        }
    }
}
