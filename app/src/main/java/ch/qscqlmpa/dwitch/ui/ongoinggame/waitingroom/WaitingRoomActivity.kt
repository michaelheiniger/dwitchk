package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.common.InfoDialog
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestCommand
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostCommand
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostViewModel
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class WaitingRoomActivity : OngoingGameBaseActivity() {

    private lateinit var wrViewModel: WaitingRoomViewModel
    private lateinit var wrHostViewModel: WaitingRoomHostViewModel
    private lateinit var wrGuestViewModel: WaitingRoomGuestViewModel
    private lateinit var connectionHostViewModel: ConnectionHostViewModel
    private lateinit var connectionGuestViewModel: ConnectionGuestViewModel
    private lateinit var playerRole: PlayerRole

    @Composable
    private fun ActivityScreenForHost(
        wrViewModel: WaitingRoomViewModel,
        wrHostViewModel: WaitingRoomHostViewModel,
        connectionViewModel: ConnectionHostViewModel
    ) {
        MaterialTheme {
            Surface(color = Color.White) {
                val players = wrViewModel.players.observeAsState(emptyList()).value
                val launchGameEnabled = wrHostViewModel.canGameBeLaunched.observeAsState(false).value
                val connectionStatus = connectionViewModel.connectionStatus.observeAsState().value
                WaitingRoomHostScreen(
                    players,
                    launchGameEnabled,
                    connectionStatus,
                    wrHostViewModel::launchGame,
                    wrHostViewModel::cancelGame,
                    connectionViewModel::reconnect
                )
            }
        }
    }

    @Composable
    private fun ActivityScreenForGuest(
        wrViewModel: WaitingRoomViewModel,
        wrGuestViewModel: WaitingRoomGuestViewModel,
        connectionViewModel: ConnectionGuestViewModel
    ) {
        MaterialTheme {
            Surface(color = Color.White) {
                val players = wrViewModel.players.observeAsState(emptyList()).value
                val readyControl = wrGuestViewModel.ready.observeAsState(UiCheckboxModel(checked = false, enabled = false)).value
                val command = wrGuestViewModel.commands.observeAsState().value
                val connectionStatus = connectionViewModel.communicationState.observeAsState().value
                WaitingRoomGuestScreen(
                    players = players,
                    ready = readyControl,
                    connectionStatus = connectionStatus,
                    onReadyClick = wrGuestViewModel::updateReadyState,
                    onLeaveClick = wrGuestViewModel::leaveGame,
                    onReconnectClick = connectionViewModel::reconnect
                )
                when (command) {
                    WaitingRoomGuestCommand.NotifyUserGameCanceled -> {
                        InfoDialog(
                            title = R.string.info_dialog_title,
                            text = R.string.game_canceled_by_host,
                            onOkClick = { wrGuestViewModel.acknowledgeGameCanceledEvent() }
                        )
                    }
                    else -> { // Nothing to do
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        playerRole = PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE)!!)

        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        wrViewModel = viewModelProvider.get(WaitingRoomViewModel::class.java)

        when (playerRole) {
            PlayerRole.HOST -> {
                wrHostViewModel = viewModelProvider.get(WaitingRoomHostViewModel::class.java)
                connectionHostViewModel = ViewModelProvider(this, viewModelFactory).get(ConnectionHostViewModel::class.java)
                setupHostCommands(wrHostViewModel)
                setContent {
                    ActivityScreenForHost(
                        wrViewModel = wrViewModel,
                        wrHostViewModel = wrHostViewModel,
                        connectionViewModel = connectionHostViewModel
                    )
                }
            }

            PlayerRole.GUEST -> {
                wrGuestViewModel = viewModelProvider.get(WaitingRoomGuestViewModel::class.java)
                connectionGuestViewModel = viewModelProvider.get(ConnectionGuestViewModel::class.java)
                setupGuestCommands(wrGuestViewModel)
                setContent {
                    ActivityScreenForGuest(
                        wrViewModel = wrViewModel,
                        wrGuestViewModel = wrGuestViewModel,
                        connectionViewModel = connectionGuestViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        wrViewModel.onStart()
        when (playerRole) {
            PlayerRole.HOST -> {
                wrHostViewModel.onStart()
                connectionHostViewModel.onStart()
            }
            PlayerRole.GUEST -> {
                wrGuestViewModel.onStart()
                connectionGuestViewModel.onStart()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        wrViewModel.onStop()
        when (playerRole) {
            PlayerRole.HOST -> {
                wrHostViewModel.onStop()
                connectionHostViewModel.onStop()
            }
            PlayerRole.GUEST -> {
                wrGuestViewModel.onStop()
                connectionGuestViewModel.onStop()
            }
        }
    }

    private fun setupHostCommands(wrHostViewModel: WaitingRoomHostViewModel) {
        wrHostViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    WaitingRoomHostCommand.NavigateToHomeScreen -> MainActivity.start(this)
                    WaitingRoomHostCommand.NavigateToGameRoomScreen -> GameRoomActivity.startForHost(this)
                }
                finish()
            }
        )
    }

    private fun setupGuestCommands(wrGuestViewModel: WaitingRoomGuestViewModel) {
        wrGuestViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    WaitingRoomGuestCommand.NavigateToHomeScreen -> {
                        MainActivity.start(this)
                        finish()
                    }
                    WaitingRoomGuestCommand.NavigateToGameRoomScreen -> {
                        GameRoomActivity.startForGuest(this)
                        finish()
                    }
                    else -> {
                        // Nothing to do
                    }
                }
            }
        )
    }

    companion object {
        fun startActivityForHost(context: Context) {
            val intent = Intent(context, WaitingRoomActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.HOST.name)
            context.startActivity(intent)
        }

        fun startActivityForGuest(context: Context) {
            val intent = Intent(context, WaitingRoomActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.GUEST.name)
            context.startActivity(intent)
        }
    }
}
