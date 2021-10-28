package ch.qscqlmpa.dwitch.ui.home.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScanResult
import ch.qscqlmpa.dwitch.ui.qrcodescanner.ScanQrCodeResultContract
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import org.joda.time.DateTime
import java.util.*

@Preview
@Composable
fun HomeBodyPreview() {
    val advertisedGame = LoadedData.Success(
        listOf(
            GameAdvertisingInfo(false, "Game 1", GameCommonId(UUID.randomUUID()), "192.168.1.1", 8889),
            GameAdvertisingInfo(false, "Game 2", GameCommonId(UUID.randomUUID()), "192.168.1.2", 8889),
            GameAdvertisingInfo(false, "Game 3", GameCommonId(UUID.randomUUID()), "192.168.1.3", 8889)
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
    PreviewContainer {
        HomeBody(
            notification = HomeNotification.None,
            advertisedGames = advertisedGame,
            resumableGames = resumableGameResponse,
            loading = false,
            onCreateNewGameClick = {},
            toggleDarkTheme = {},
            onJoinGameClick = {},
            onResumableGameClick = {},
            onQrCodeScan = {}
        )
    }
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    toggleDarkTheme: () -> Unit
) {
    DisposableEffect(homeViewModel) {
        homeViewModel.onStart()
        onDispose { homeViewModel.onStop() }
    }

    HomeBody(
        notification = homeViewModel.notification.value,
        advertisedGames = homeViewModel.advertisedGames.value,
        resumableGames = homeViewModel.resumableGames.value,
        loading = homeViewModel.loading.value,
        toggleDarkTheme = toggleDarkTheme,
        onCreateNewGameClick = homeViewModel::createNewGame,
        onJoinGameClick = homeViewModel::joinGame,
        onResumableGameClick = { game -> homeViewModel.resumeGame(game) },
        onQrCodeScan = { gameAd -> homeViewModel.load(gameAd) }
    )
}

@Composable
fun HomeBody(
    notification: HomeNotification,
    advertisedGames: LoadedData<List<GameAdvertisingInfo>>,
    resumableGames: LoadedData<List<ResumableGameInfo>>,
    loading: Boolean,
    toggleDarkTheme: () -> Unit,
    onCreateNewGameClick: () -> Unit,
    onJoinGameClick: (GameAdvertisingInfo) -> Unit,
    onResumableGameClick: (ResumableGameInfo) -> Unit,
    onQrCodeScan: (GameAdvertisingInfo) -> Unit
) {
    Scaffold(
        topBar = {
            DwitchTopBar(
                title = R.string.app_name,
                navigationIcon = null,
                actions = listOf(ToggleDarkTheme),
                onActionClick = { toggleDarkTheme() }
            )
        }
    ) {
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
                Column(Modifier.fillMaxSize()) {
                    JoinGameWithQrCode(onQrCodeScan = onQrCodeScan)
                    Spacer(Modifier.height(16.dp))
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
        Notification(notification)
        if (loading) LoadingDialog()
    }
}

@Composable
private fun JoinGameWithQrCode(onQrCodeScan: (GameAdvertisingInfo) -> Unit) {
    val showErrorDialog = remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ScanQrCodeResultContract()) { scanResult ->
        when (scanResult) {
            QrCodeScanResult.NoResult -> {
            } // Nothing to do
            QrCodeScanResult.Error -> showErrorDialog.value = true
            is QrCodeScanResult.Success<*> -> onQrCodeScan(scanResult.value as GameAdvertisingInfo)
        }
    }

    if (showErrorDialog.value) {
        InfoDialog(
            title = R.string.dialog_error_title,
            text = R.string.error_scanning_qr_code,
            onOkClick = { showErrorDialog.value = false }
        )
    }

    OutlinedButton(
        onClick = { launcher.launch(Unit) },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(UiTags.joinGameQrCode)
    ) { Text(stringResource(R.string.join_game_with_qr_code)) }
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
    advertisedGames: LoadedData<List<GameAdvertisingInfo>>,
    onJoinGameClick: (GameAdvertisingInfo) -> Unit
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
    advertisedGames: List<GameAdvertisingInfo>,
    onJoinGameClick: (GameAdvertisingInfo) -> Unit
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

@Composable
private fun Notification(notification: HomeNotification) {
    when (notification) {
        HomeNotification.None -> {
            // Nothing to do
        }
        HomeNotification.ErrorJoiningGame -> {
            InfoDialog(
                title = R.string.dialog_error_title,
                text = R.string.error_joining_game_text,
                onOkClick = {} // Nothing to do
            )
        }
    }
}
