package ch.qscqlmpa.dwitch.ui.home.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                listOf("Ned Stark", "Arya Stark", "Sandor Clegane", "Davos Seaworth", "Barristan Selmy")
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
            onDeleteExistingGame = {},
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
        onDeleteExistingGame = { game -> homeViewModel.deleteExistingGame(game) },
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
    onDeleteExistingGame: (ResumableGameInfo) -> Unit,
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
                .padding(8.dp)
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
                    Spacer(Modifier.height(8.dp))
                    GameCreation(onCreateNewGameClick = onCreateNewGameClick)
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                ResumableGamesContainer(resumableGames, onResumableGameClick, onDeleteExistingGame)
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
            LoadedData.Loading -> NoGameDiscoveredYet()
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {

        if (advertisedGames.isEmpty()) {
            item {
                ListItem(text = { NoGameDiscoveredYet() })
            }
        }

        items(advertisedGames) { game ->
            val contentDescription = stringResource(R.string.join_specific_game_cd, game.gameName)
            Card(
                elevation = animateDpAsState(0.dp).value,
                modifier = Modifier
                    .clickable { onJoinGameClick(game) }
                    .semantics { this.contentDescription = contentDescription }
            ) {
                ListItem(
                    text = { Text(text = game.gameName) },
                    secondaryText = { Text(text = game.gameIpAddress) }
                )
            }
        }
    }
}

@Composable
private fun NoGameDiscoveredYet() {
    Text(stringResource(R.string.no_game_discovered_yet))
}

@Composable
fun ResumableGamesContainer(
    resumableGames: LoadedData<List<ResumableGameInfo>>,
    onResumableGameClick: (ResumableGameInfo) -> Unit,
    onDeleteExistingGame: (ResumableGameInfo) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        when (resumableGames) {
            is LoadedData.Success -> ResumableGames(resumableGames.data, onResumableGameClick, onDeleteExistingGame)
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
    onResumableGameClick: (ResumableGameInfo) -> Unit,
    onDeleteExistingGame: (ResumableGameInfo) -> Unit
) {
    if (resumableGames.isNotEmpty()) {
        ResumableGameTitle()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items(resumableGames) { game ->
                val dismissState = rememberDismissState()
                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                    onDeleteExistingGame(game)
                }
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier.testTag("${UiTags.deleteExistingGame}-${game.name}"),
                    directions = setOf(DismissDirection.EndToStart),
                    dismissThresholds = { FractionalThreshold(0.5f) },
                    background = {
                        dismissState.dismissDirection ?: return@SwipeToDismiss
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> Color.LightGray
                                DismissValue.DismissedToEnd -> Color.Green
                                DismissValue.DismissedToStart -> Color.Red
                            }
                        )
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                        )

                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.delete_existing_game, game.name),
                                color = Color.White,
                                fontSize = 20.sp,
                                modifier = Modifier.scale(scale)
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                tint = Color.White,
                                contentDescription = stringResource(R.string.delete_existing_game),
                                modifier = Modifier.scale(scale)
                            )
                        }
                    },
                    dismissContent = {
                        Card(
                            elevation = animateDpAsState(if (dismissState.dismissDirection != null) 4.dp else 0.dp).value,
                            modifier = Modifier.clickable { onResumableGameClick(game) }
                        ) {
                            ListItem(
                                overlineText = {
                                    Text(text = game.creationDate.toStringEuFormat())
                                },
                                text = {
                                    Text(text = game.name)
                                },
                                secondaryText = {
                                    Text(text = game.playersName.joinToString(", "))
                                },
                                singleLineSecondaryText = false
                            )
                        }
                    }
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
