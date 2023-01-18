package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

import android.graphics.Bitmap
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ingame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomPlayers
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Preview
@Composable
private fun WaitingRoomHostBodyPreview() {

    val qrCode = buildSampleQrCode()

    PreviewContainer {
        WaitingRoomHostBody(
            toolbarTitle = "Dwiiitch",
            showAddComputerPlayer = true,
            players = listOf(
                PlayerWrUi(1L, "Aragorn", connected = true, ready = true, kickable = false),
                PlayerWrUi(2L, "Boromir", connected = true, ready = false, kickable = true),
                PlayerWrUi(3L, "Gimli", connected = false, ready = false, kickable = true)
            ),
            gameQrCode = qrCode,
            launchGameEnabled = false,
            connectionStatus = HostCommunicationState.OfflineFailed(connectedToWlan = true),
            launchingGame = false,
            cancelingGame = false,
            onLaunchGameClick = {},
            onCancelGameClick = {},
            onReconnectClick = {}
        )
    }
}

@Composable
fun WaitingRoomHostBody(
    waitingRoomViewModel: WaitingRoomViewModel,
    hostViewModel: WaitingRoomHostViewModel,
    connectionViewModel: ConnectionHostViewModel
) {
    DisposableEffect(waitingRoomViewModel, hostViewModel, connectionViewModel) {
        waitingRoomViewModel.onStart()
        hostViewModel.onStart()
        connectionViewModel.onStart()
        onDispose {
            waitingRoomViewModel.onStop()
            hostViewModel.onStop()
            connectionViewModel.onStop()
        }
    }

    WaitingRoomHostBody(
        toolbarTitle = waitingRoomViewModel.toolbarTitle.value,
        showAddComputerPlayer = waitingRoomViewModel.canComputerPlayersBeAdded.value,
        players = waitingRoomViewModel.players.value,
        gameQrCode = hostViewModel.gameQrCode.value,
        launchGameEnabled = hostViewModel.canGameBeLaunched.value,
        connectionStatus = connectionViewModel.connectionStatus.value,
        launchingGame = hostViewModel.launchingGame.value,
        cancelingGame = hostViewModel.cancelingGame.value,
        onLaunchGameClick = hostViewModel::launchGame,
        onCancelGameClick = hostViewModel::cancelGame,
        onReconnectClick = connectionViewModel::reconnect,
        onAddComputerPlayer = hostViewModel::addComputerPlayer,
        onKickPlayer = hostViewModel::kickPlayer
    )
}

@Composable
fun WaitingRoomHostBody(
    toolbarTitle: String,
    showAddComputerPlayer: Boolean,
    players: List<PlayerWrUi>,
    gameQrCode: Bitmap?,
    launchGameEnabled: Boolean,
    connectionStatus: HostCommunicationState,
    launchingGame: Boolean,
    cancelingGame: Boolean,
    onLaunchGameClick: () -> Unit,
    onCancelGameClick: () -> Unit,
    onReconnectClick: () -> Unit,
    onAddComputerPlayer: () -> Unit = {},
    onKickPlayer: (PlayerWrUi) -> Unit = {}
) {
    val showCancelGameConfirmationDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            DwitchTopBar(
                title = toolbarTitle,
                navigationIcon = NavigationIcon(
                    icon = R.drawable.ic_baseline_exit_to_app_24,
                    contentDescription = R.string.cancel_game,
                    onClick = { showCancelGameConfirmationDialog.value = true }
                )
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(innerPadding)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(Alignment.Top)) {
                WaitingRoomPlayers(
                    players = players,
                    showAddComputerPlayer = showAddComputerPlayer,
                    onAddComputerPlayer = onAddComputerPlayer,
                    onKickPlayer = onKickPlayer
                )
            }
            Spacer(Modifier.height(16.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(Alignment.Top)) {
                HostControls(
                    launchGameEnabled = launchGameEnabled,
                    onLaunchGameClick = onLaunchGameClick,
                )
                GameQrCode(gameQrCode)
            }
        }
        when {
            launchingGame -> LoadingDialog(R.string.launching_game)
            cancelingGame -> LoadingDialog(R.string.canceling_game)
            showCancelGameConfirmationDialog.value -> {
                ConfirmationDialog(
                    title = R.string.dialog_info_title,
                    text = R.string.host_cancel_game_confirmation,
                    onConfirmClick = onCancelGameClick,
                    onClosing = { showCancelGameConfirmationDialog.value = false }
                )
            }
            else -> CommunicationHost(
                state = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = onCancelGameClick
            )
        }
    }
}

@Composable
private fun HostControls(
    launchGameEnabled: Boolean,
    onLaunchGameClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onLaunchGameClick,
            enabled = launchGameEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.launchGameControl)
        ) {
            Text(stringResource(R.string.launch_game), color = Color.White)
        }
    }
}

@Composable
private fun GameQrCode(gameQrCode: Bitmap?) {
    val showQrCodeInModal = remember { mutableStateOf(false) }
    if (gameQrCode != null) {
        Column(Modifier.fillMaxWidth()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.game_ad_qr_code_hint),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Image(
                bitmap = gameQrCode.asImageBitmap(),
                contentDescription = stringResource(R.string.game_ad_qr_code_cd),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clickable { showQrCodeInModal.value = true }
            )
        }
        if (showQrCodeInModal.value) {
            InfoDialog(
                text = R.string.game_ad_qr_code_hint,
                content = {
                    Image(
                        bitmap = gameQrCode.asImageBitmap(),
                        contentDescription = "Game advertisement QR Code",
                        modifier = Modifier.size(300.dp)
                    )
                },
                onCloseClick = { showQrCodeInModal.value = false }
            )
        }
    }
}

fun buildSampleQrCode(): Bitmap? {
    val content = "https://www.github.com/michaelheiniger/dwitchk"
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val qrCode = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
    for (x in 0 until bitMatrix.width) {
        for (y in 0 until bitMatrix.height) {
            qrCode.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return qrCode
}
