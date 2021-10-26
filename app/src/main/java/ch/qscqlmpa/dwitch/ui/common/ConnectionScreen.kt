package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState

@Preview
@Composable
private fun CommunicationHostPreview() {
    PreviewContainer {
        CommunicationHost(
            state = HostCommunicationState.OfflineFailed(connectedToWlan = true),
            onReconnectClick = {},
            onAbortClick = {}
        )
    }
}

@Preview
@Composable
private fun CommunicationGuestPreview() {
    PreviewContainer {
        CommunicationGuest(
            state = GuestCommunicationState.Error(connectedToWlan = true),
            onReconnectClick = {},
            onAbortClick = {}
        )
    }
}

@Composable
fun CommunicationHost(
    state: HostCommunicationState,
    onReconnectClick: () -> Unit,
    onAbortClick: () -> Unit
) {
    val connectionInfo = when (state) {
        HostCommunicationState.Starting -> CommunicationDialogInfo(
            message = R.string.host_connecting,
            showLoading = true,
            reconnectBtnEnabled = false
        )
        HostCommunicationState.Online -> null
        is HostCommunicationState.OfflineDisconnected -> {
            if (state.connectedToWlan) {
                CommunicationDialogInfo(
                    message = R.string.not_listening_for_guests,
                    showLoading = false,
                    reconnectBtnEnabled = true
                )
            } else {
                CommunicationDialogInfo(
                    message = R.string.not_connected_to_a_wlan,
                    showLoading = false,
                    reconnectBtnEnabled = false
                )
            }
        }
        is HostCommunicationState.OfflineFailed -> {
            if (state.connectedToWlan) {
                CommunicationDialogInfo(
                    message = R.string.host_connection_error,
                    showLoading = false,
                    reconnectBtnEnabled = true
                )
            } else {
                CommunicationDialogInfo(
                    message = R.string.not_connected_to_a_wlan,
                    showLoading = false,
                    reconnectBtnEnabled = false
                )
            }
        }
    }
    if (connectionInfo != null) {
        CommunicationDialog(
            communicationDialogInfo = connectionInfo,
            abortDescription = R.string.leave_game,
            onReconnectClick = onReconnectClick,
            onAbortClick = onAbortClick
        )
    }
}

@Composable
fun CommunicationGuest(
    state: GuestCommunicationState,
    onReconnectClick: () -> Unit,
    onAbortClick: () -> Unit
) {
    val connectionInfo = when (state) {
        GuestCommunicationState.Connecting -> CommunicationDialogInfo(
            message = R.string.guest_connecting,
            showLoading = true,
            reconnectBtnEnabled = false
        )
        GuestCommunicationState.Connected -> null
        is GuestCommunicationState.Disconnected -> {
            if (state.connectedToWlan) {
                CommunicationDialogInfo(
                    message = R.string.disconnected_from_host,
                    showLoading = false,
                    reconnectBtnEnabled = true
                )
            } else {
                CommunicationDialogInfo(
                    message = R.string.not_connected_to_a_wlan,
                    showLoading = false,
                    reconnectBtnEnabled = false // Must first connect to a WLAN
                )
            }
        }
        is GuestCommunicationState.Error -> {
            if (state.connectedToWlan) {
                CommunicationDialogInfo(
                    message = R.string.guest_connection_error,
                    showLoading = false,
                    reconnectBtnEnabled = true
                )
            } else {
                CommunicationDialogInfo(
                    message = R.string.not_connected_to_a_wlan,
                    showLoading = false,
                    reconnectBtnEnabled = false // Must first connect to a WLAN
                )
            }
        }
    }
    if (connectionInfo != null) {
        CommunicationDialog(
            communicationDialogInfo = connectionInfo,
            abortDescription = R.string.leave_game,
            onReconnectClick = onReconnectClick,
            onAbortClick = onAbortClick
        )
    }
}

@Composable
private fun Status(statusResource: Int) {
    Text(
        text = stringResource(statusResource),
        color = MaterialTheme.colors.primary
    )
}

@Composable
private fun ReconnectionControls(
    showLoading: Boolean,
    reconnectBtnEnabled: Boolean,
    onReconnectClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (showLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
        }
        Button(
            enabled = reconnectBtnEnabled,
            onClick = onReconnectClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.reconnect)
        ) {
            Text(stringResource(R.string.reconnect))
        }
    }
}

@Preview
@Composable
private fun ConnectionDialogPreview() {
    PreviewContainer {
        CommunicationDialog(
            communicationDialogInfo = CommunicationDialogInfo(
                message = R.string.guest_connection_error,
                showLoading = true,
                reconnectBtnEnabled = true
            ),
            abortDescription = R.string.leave_game,
            onReconnectClick = {},
            onAbortClick = {}
        )
    }
}

@Composable
private fun CommunicationDialog(
    communicationDialogInfo: CommunicationDialogInfo,
    abortDescription: Int,
    onReconnectClick: () -> Unit,
    onAbortClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onAbortClick,
        properties = DialogProperties(),
        content = {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface,
                contentColor = contentColorFor(MaterialTheme.colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Status(communicationDialogInfo.message)
                    Spacer(Modifier.height(16.dp))
                    ReconnectionControls(
                        showLoading = communicationDialogInfo.showLoading,
                        reconnectBtnEnabled = communicationDialogInfo.reconnectBtnEnabled,
                        onReconnectClick = onReconnectClick
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onAbortClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(abortDescription))
                    }
                }
            }
        }
    )
}

private data class CommunicationDialogInfo(
    val message: Int,
    val showLoading: Boolean,
    val reconnectBtnEnabled: Boolean
)
