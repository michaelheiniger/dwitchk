package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.ConfirmationDialog
import ch.qscqlmpa.dwitch.ui.common.InfoDialog
import ch.qscqlmpa.dwitch.ui.common.LoadingDialog
import ch.qscqlmpa.dwitch.ui.common.toolbarDefaultTitle
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestDestination
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestNotification
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostDestination
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
    private var showConfirmationDialog = MutableLiveData(false)

    @Composable
    private fun ActivityScreenForHost(
        wrViewModel: WaitingRoomViewModel,
        wrHostViewModel: WaitingRoomHostViewModel,
        connectionViewModel: ConnectionHostViewModel
    ) {
        val toolbarTitle = wrViewModel.toolbarTitle.observeAsState(toolbarDefaultTitle).value
        val showAddComputerPlayer = wrViewModel.canComputerPlayersBeAdded.observeAsState(false).value
        val players = wrViewModel.players.observeAsState(emptyList()).value
        val launchGameEnabled = wrHostViewModel.canGameBeLaunched.observeAsState(false).value
        val connectionStatus = connectionViewModel.connectionStatus.observeAsState().value
        WaitingRoomHostScreen(
            toolbarTitle = toolbarTitle,
            showAddComputerPlayer = showAddComputerPlayer,
            players = players,
            launchGameEnabled = launchGameEnabled,
            connectionStatus = connectionStatus,
            onLaunchGameClick = wrHostViewModel::launchGame,
            onCancelGameClick = { showConfirmationDialog.value = true },
            onReconnectClick = connectionViewModel::reconnect,
            onAddComputerPlayer = wrHostViewModel::addComputerPlayer,
            onKickPlayer = wrHostViewModel::kickPlayer
        )
        if (showConfirmationDialog.observeAsState().value == true) {
            ConfirmationDialog(
                title = R.string.info_dialog_title,
                text = R.string.host_cancel_game_confirmation,
                onConfirmClick = { wrHostViewModel.cancelGame() },
                onCancelClick = { showConfirmationDialog.value = false }
            )
        }
        if (wrHostViewModel.loading.observeAsState(false).value) LoadingDialog()
    }

    @Composable
    private fun ActivityScreenForGuest(
        wrViewModel: WaitingRoomViewModel,
        wrGuestViewModel: WaitingRoomGuestViewModel,
        connectionViewModel: ConnectionGuestViewModel
    ) {
        val toolbarTitle = wrViewModel.toolbarTitle.observeAsState(toolbarDefaultTitle).value
        val players = wrViewModel.players.observeAsState(emptyList()).value
        val readyControl = wrGuestViewModel.ready.observeAsState(UiCheckboxModel(checked = false, enabled = false)).value
        val connectionStatus = connectionViewModel.connectionStatus.observeAsState().value
        WaitingRoomGuestScreen(
            toolbarTitle = toolbarTitle,
            players = players,
            ready = readyControl,
            connectionStatus = connectionStatus,
            onReadyClick = wrGuestViewModel::updateReadyState,
            onLeaveClick = { showConfirmationDialog.value = true },
            onReconnectClick = connectionViewModel::reconnect
        )
        when (wrGuestViewModel.notifications.observeAsState().value) {
            WaitingRoomGuestNotification.NotifyGameCanceled -> {
                InfoDialog(
                    title = R.string.info_dialog_title,
                    text = R.string.game_canceled_by_host,
                    onOkClick = { wrGuestViewModel.acknowledgeGameCanceledEvent() }
                )
            }
            WaitingRoomGuestNotification.NotifyPlayerKickedOffGame -> {
                InfoDialog(
                    title = R.string.info_dialog_title,
                    text = R.string.you_have_been_kick,
                    onOkClick = { wrGuestViewModel.acknowledgeKickOffGame() }
                )
            }
            else -> { // Nothing to do
            }
        }
        if (showConfirmationDialog.observeAsState().value == true) {
            ConfirmationDialog(
                title = R.string.info_dialog_title,
                text = R.string.guest_leaves_game_confirmation,
                onConfirmClick = { wrGuestViewModel.leaveGame() },
                onCancelClick = { showConfirmationDialog.value = false }
            )
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
                setupHostNavigation(wrHostViewModel)
                setContent {
                    ActivityScreenContainer {
                        ActivityScreenForHost(
                            wrViewModel = wrViewModel,
                            wrHostViewModel = wrHostViewModel,
                            connectionViewModel = connectionHostViewModel
                        )
                    }
                }
            }

            PlayerRole.GUEST -> {
                wrGuestViewModel = viewModelProvider.get(WaitingRoomGuestViewModel::class.java)
                connectionGuestViewModel = viewModelProvider.get(ConnectionGuestViewModel::class.java)
                setupGuestNavigation(wrGuestViewModel)
                setContent {
                    ActivityScreenContainer {
                        ActivityScreenForGuest(
                            wrViewModel = wrViewModel,
                            wrGuestViewModel = wrGuestViewModel,
                            connectionViewModel = connectionGuestViewModel
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        showConfirmationDialog.value = true
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

    private fun setupHostNavigation(wrHostViewModel: WaitingRoomHostViewModel) {
        wrHostViewModel.navigation.observe(
            this,
            { command ->
                when (command) {
                    WaitingRoomHostDestination.NavigateToHomeScreen -> {
                        finish()
                        MainActivity.start(this)
                    }
                    WaitingRoomHostDestination.NavigateToGameRoomScreen -> {
                        finish()
                        GameRoomActivity.startForHost(this)
                    }
                    else -> {
                        // Nothing do to
                    }
                }
            }
        )
    }

    private fun setupGuestNavigation(wrGuestViewModel: WaitingRoomGuestViewModel) {
        wrGuestViewModel.navigation.observe(
            this,
            { command ->
                when (command) {
                    WaitingRoomGuestDestination.NavigateToHomeScreen -> {
                        finish()
                        MainActivity.start(this)
                    }
                    WaitingRoomGuestDestination.NavigateToGameRoomScreen -> {
                        finish()
                        GameRoomActivity.startForGuest(this)
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
            startActivity(context, PlayerRole.HOST)
        }

        fun startActivityForGuest(context: Context) {
            startActivity(context, PlayerRole.GUEST)
        }

        private fun startActivity(context: Context, playerRole: PlayerRole) {
            val intent = Intent(context, WaitingRoomActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, playerRole.name)
            intent.flags = FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
