package ch.qscqlmpa.dwitch.ui.home.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
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
    ActivityScreenContainer {
        HomeBody(
            advertisedGames = advertisedGame,
            resumableGames = resumableGameResponse,
            onCreateNewGameClick = {},
            onJoinGameClick = {},
            onResumableGameClick = {}
        )
    }
}

@Composable
fun HomeScreen(
    vmFactory: ViewModelFactory,
    onCreateNewGameClick: () -> Unit,
    onJoinNewGameEvent: (AdvertisedGame) -> Unit,
    onNavigateToGame: () -> Unit
) {
    val viewModel = viewModel<HomeViewModel>(factory = vmFactory)
    DisposableEffect(key1 = viewModel) {
        viewModel.onStart()
        onDispose { viewModel.onStop() }
    }

    val advertisedGames = viewModel.advertisedGames.observeAsState(LoadedData.Loading)
    val resumableGames = viewModel.resumableGames.observeAsState(LoadedData.Loading)
    HomeBody(
        advertisedGames = advertisedGames.value,
        resumableGames = resumableGames.value,
        onJoinGameClick = { game -> viewModel.joinGame(game) },
        onCreateNewGameClick = onCreateNewGameClick,
        onResumableGameClick = { game -> viewModel.resumeGame(game) }
    )
    if (viewModel.loading.observeAsState(false).value) LoadingDialog()
    Navigation(
        destination = viewModel.navigation.value,
        onJoinNewGameEvent = onJoinNewGameEvent,
        onNavigateToGame = onNavigateToGame
    )
}

@Composable
fun Navigation(
    destination: HomeDestination,
    onJoinNewGameEvent: (AdvertisedGame) -> Unit,
    onNavigateToGame: () -> Unit
) {
    when (destination) {
        HomeDestination.CurrentScreen -> {
            // Nothing to do
        }
        is HomeDestination.JoinNewGame -> onJoinNewGameEvent(destination.game)
        else -> onNavigateToGame()
    }
}

@Composable
fun HomeBody(
    advertisedGames: LoadedData<List<AdvertisedGame>>,
    resumableGames: LoadedData<List<ResumableGameInfo>>,
    onCreateNewGameClick: () -> Unit,
    onJoinGameClick: (AdvertisedGame) -> Unit,
    onResumableGameClick: (ResumableGameInfo) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        DwitchTopBar(title = R.string.app_name)
        Column(
            Modifier
                .fillMaxSize()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)

        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AdvertisedGameContainer(advertisedGames, onJoinGameClick)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End) {
                    GameCreation(onCreateNewGameClick = onCreateNewGameClick)
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                ResumableGamesContainer(resumableGames, onResumableGameClick)
            }
        }
    }
}

@Composable
private fun GameCreation(onCreateNewGameClick: () -> Unit) {
    Button(
        onClick = onCreateNewGameClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(UiTags.createGame)
    ) { Text(stringResource(R.string.create_new_game)) }
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
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.advertisedGames)
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        if (advertisedGames.isEmpty()) {
            item { ListeningForAdvertisedGames() }
        }

        items(advertisedGames) { game ->
            val text = if (BuildConfig.DEBUG) "${game.gameName} (${game.gameIpAddress})" else game.gameName
            val contentDescription = stringResource(R.string.join_specific_game_cd, game.gameName)
            Text(
                text = text,
                modifier = Modifier
                    .clickable { onJoinGameClick(game) }
                    .semantics { this.contentDescription = contentDescription }
                    .fillMaxWidth()
            )
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
        when (resumableGames) {
            is LoadedData.Success -> ResumableGames(resumableGames.data, onResumableGameClick)
            is LoadedData.Failed -> {
                ResumableGameTitle()
                Text(stringResource(R.string.loading_resumable_games_failed), color = Color.Red)
            }
            else -> {
                // Nothing to do
            }
        }
    }
}

@Composable
private fun ResumableGameTitle() {
    Text(
        stringResource(R.string.resumable_games),
        fontSize = 32.sp,
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(UiTags.resumableGames)
    )
}

@Composable
private fun ResumableGames(
    resumableGames: List<ResumableGameInfo>,
    onResumableGameClick: (ResumableGameInfo) -> Unit
) {
    if (resumableGames.isNotEmpty()) {
        ResumableGameTitle()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(resumableGames) { game ->
                Text(
                    text = "${game.name} (${game.creationDate.toStringEuFormat()} - ${game.playersName.joinToString(", ")})",
                    Modifier
                        .clickable { onResumableGameClick(game) }
                        .fillMaxWidth()
                )
            }
        }
    }
}
