package ch.qscqlmpa.dwitch.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CommunicationHostScreenPreview() {
    ConnectionHostScreen(HostCommunicationState.Error) {}
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CommunicationGuestScreenPreview() {
    ConnectionGuestScreen(GuestCommunicationState.Error) {}
}

@Composable
fun ConnectionHostScreen(
    status: HostCommunicationState?,
    onReconnectClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        when (status) {
            HostCommunicationState.Open -> {
                if (BuildConfig.DEBUG) {
                    Status(R.string.listening_for_guests)
                }
            }
            HostCommunicationState.Closed -> {
                Status(R.string.not_listening_for_guests)
                ReconnectionControls { onReconnectClick() }
            }
            HostCommunicationState.Error -> {
                Status(R.string.host_connection_error)
                ReconnectionControls { onReconnectClick() }
            }
            HostCommunicationState.Opening -> {
                Status(R.string.host_connecting)
                ReconnectionControls(true) { onReconnectClick() }
            }
            else -> {
            }
        }
    }
}

@Composable
fun ConnectionGuestScreen(
    status: GuestCommunicationState?,
    onReconnectClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        when (status) {
            GuestCommunicationState.Connected -> {
                if (BuildConfig.DEBUG) {
                    Status(R.string.connected_to_host)
                }
            }
            GuestCommunicationState.Disconnected -> {
                Status(R.string.disconnected_from_host)
                ReconnectionControls { onReconnectClick() }
            }
            GuestCommunicationState.Error -> {
                Status(R.string.guest_connection_error)
                ReconnectionControls { onReconnectClick() }
            }
            GuestCommunicationState.Connecting -> {
                Status(R.string.guest_connecting)
                ReconnectionControls(true) { onReconnectClick() }
            }
            else -> {
            }
        }
    }
}

@Composable
private fun Status(statusResource: Int) {
    Text(
        text = stringResource(statusResource),
        color = MaterialTheme.colors.secondary
    )
}

@Composable
private fun ReconnectionControls(
    connectionOnGoing: Boolean = false,
    onReconnectClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                enabled = !connectionOnGoing,
                onClick = onReconnectClick,
                modifier = Modifier.padding(
                    start = 0.dp,
                    top = 0.dp,
                    end = 16.dp,
                    bottom = 0.dp
                )
            ) {
                Text(text = stringResource(R.string.reconnect))
            }
            if (connectionOnGoing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}
