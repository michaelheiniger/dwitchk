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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import org.joda.time.DateTime

@Preview(showBackground = false)
@Composable
fun HomeScreenPreview() {
    val advertisedGameResponse = AdvertisedGamesResponse(
        Status.SUCCESS,
        listOf(
            AdvertisedGame(false, "Game 1", GameCommonId(1), "192.168.1.1", 8889),
            AdvertisedGame(false, "Game 2", GameCommonId(2), "192.168.1.2", 8889),
            AdvertisedGame(false, "Game 3", GameCommonId(3), "192.168.1.3", 8889)
        ),
        null
    )

    val resumableGameResponse = ResumableGamesResponse(
        Status.SUCCESS,
        listOf(
            ResumableGameInfo(1, DateTime.parse("2020-07-26T01:20+02:00"), "LOTR", listOf("Aragorn", "Legolas", "Gimli")),
            ResumableGameInfo(
                2,
                DateTime.parse("2021-01-01T01:18+02:00"),
                "GoT",
                listOf("Ned Stark", "Arya Stark", "Sandor Clegane")
            )
        ),
        null
    )
    HomeScreen(
        advertisedGamesResponse = advertisedGameResponse,
        resumableGamesResponse = resumableGameResponse,
        onCreateNewGameClick = {},
        onJoinGameClick = {},
        onResumableGameClick = {}
    )
}

@Composable
fun HomeScreen(
    advertisedGamesResponse: AdvertisedGamesResponse,
    resumableGamesResponse: ResumableGamesResponse,
    onCreateNewGameClick: () -> Unit,
    onJoinGameClick: (AdvertisedGame) -> Unit,
    onResumableGameClick: (ResumableGameInfo) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)

    ) {
        AdvertisedGameContainer(advertisedGamesResponse, onJoinGameClick)
        Spacer(modifier = Modifier.height(16.dp))
        GameCreation(onCreateNewGameClick)
        Spacer(modifier = Modifier.height(16.dp))
        ResumableGamesContainer(resumableGamesResponse, onResumableGameClick)
    }
}

@Composable
private fun GameCreation(onCreateNewGameClick: () -> Unit) {
    Button(onClick = onCreateNewGameClick) { Text(stringResource(id = R.string.ma_create_game_btn)) }
}

@Composable
fun ResumableGamesContainer(
    response: ResumableGamesResponse,
    onResumableGameClick: (ResumableGameInfo) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()

    ) {
        Text(
            stringResource(id = R.string.ma_resumable_games_tv),
            fontSize = 30.sp,
            color = MaterialTheme.colors.primary
        )
        when (response.status) {
            Status.LOADING -> Text(stringResource(id = R.string.ma_loading_resumable_games))
            Status.SUCCESS -> ResumableGames(response.resumableGames, onResumableGameClick)
            Status.ERROR -> Text(stringResource(id = R.string.ma_resumable_games_loading_error_tv), color = Color.Red)
        }
    }
}

@Composable
private fun ResumableGames(
    resumableGames: List<ResumableGameInfo>,
    onResumableGameclick: (ResumableGameInfo) -> Unit
) {
    if (resumableGames.isEmpty()) {
        Text(stringResource(id = R.string.ma_no_resumable_games))
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(resumableGames) { game ->
                Text(
                    text = "${game.name} (${game.playersName.joinToString(", ")})",
                    Modifier.clickable { onResumableGameclick(game) }
                )
            }
        }
    }
}

@Composable
private fun AdvertisedGameContainer(
    response: AdvertisedGamesResponse,
    onJoinGameClick: (AdvertisedGame) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()

    ) {
        Text(
            stringResource(id = R.string.ma_advertised_games_tv),
            fontSize = 30.sp,
            color = MaterialTheme.colors.primary
        )
        when (response.status) {
            Status.LOADING -> ListeningForAdvertisedGames()
            Status.SUCCESS -> AdvertisedGames(response.advertisedGames, onJoinGameClick)
            Status.ERROR -> Text(stringResource(id = R.string.ma_advertised_games_loading_error_tv), color = Color.Red)
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
                Text(
                    text = "${game.gameName} (${game.gameIpAddress})",
                    Modifier.clickable { onJoinGameClick(game) }
                )
            }
        }
    }
}

@Composable
private fun ListeningForAdvertisedGames() {
    Text(stringResource(id = R.string.ma_listening_for_games_tv))
}