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
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CommunicationHostScreenPreview() {
    ActivityScreenContainer {
        ConnectionHostScreen(
            status = HostCommunicationState.Error,
            onReconnectClick = {},
            onAbortClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CommunicationGuestScreenPreview() {
    ActivityScreenContainer {
        ConnectionGuestScreen(
            state = GuestCommunicationState.Error,
            onReconnectClick = {},
            onAbortClick = {}
        )
    }
}

@Composable
fun ConnectionHostScreen(
    status: HostCommunicationState?,
    onReconnectClick: () -> Unit,
    onAbortClick: () -> Unit
) {
    val connectionInfo = when (status) {
        HostCommunicationState.Opening -> ConnectionInfo(R.string.host_connecting, connecting = true)
        HostCommunicationState.Closed -> ConnectionInfo(R.string.not_listening_for_guests, connecting = false)
        HostCommunicationState.Error -> ConnectionInfo(R.string.host_connection_error, connecting = false)
        else -> null
    }
    if (connectionInfo != null) {
        ConnectionDialog(
            connectionInfo = connectionInfo,
            abortDescription = R.string.leave_game,
            onReconnectClick = onReconnectClick,
            onAbortClick = onAbortClick
        )
    }
}

@Composable
fun ConnectionGuestScreen(
    state: GuestCommunicationState?,
    onReconnectClick: () -> Unit,
    onAbortClick: () -> Unit
) {
    val connectionInfo = when (state) {
        GuestCommunicationState.Connecting -> ConnectionInfo(R.string.guest_connecting, connecting = true)
        GuestCommunicationState.Disconnected -> ConnectionInfo(R.string.disconnected_from_host, connecting = false)
        GuestCommunicationState.Error -> ConnectionInfo(R.string.guest_connection_error, connecting = false)
        else -> null
    }
    if (connectionInfo != null) {
        ConnectionDialog(
            connectionInfo = connectionInfo,
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
    connecting: Boolean = false,
    onReconnectClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (connecting) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
        }
        Button(
            enabled = !connecting,
            onClick = onReconnectClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.reconnect)
        ) {
            Text(stringResource(R.string.reconnect))
        }
    }
}

@Composable
private fun ConnectionDialog(
    connectionInfo: ConnectionInfo,
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
                    Status(connectionInfo.connectionStatus)
                    Spacer(Modifier.height(16.dp))
                    ReconnectionControls(
                        connecting = connectionInfo.connecting,
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

private data class ConnectionInfo(val connectionStatus: Int, val connecting: Boolean)
