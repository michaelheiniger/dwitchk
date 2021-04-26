package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestCommand
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostCommand
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardViewModel
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class GameRoomActivity : OngoingGameBaseActivity() {

    private lateinit var viewModel: GameRoomViewModel
    private lateinit var hostViewModel: GameRoomHostViewModel
    private lateinit var guestViewModel: GameRoomGuestViewModel
    private lateinit var connectionHostViewModel: ConnectionHostViewModel
    private lateinit var connectionGuestViewModel: ConnectionGuestViewModel
    private lateinit var dashboardViewModel: PlayerDashboardViewModel
    private lateinit var playerRole: PlayerRole

    private val hostViewModels: List<BaseViewModel> by lazy {
        listOf(viewModel, hostViewModel, connectionHostViewModel, dashboardViewModel)
    }

    private val guestViewModels: List<BaseViewModel> by lazy {
        listOf(viewModel, guestViewModel, connectionGuestViewModel, dashboardViewModel)
    }

    private val relevantViewModels: List<BaseViewModel> by lazy {
        when (playerRole) {
            PlayerRole.HOST -> hostViewModels
            PlayerRole.GUEST -> guestViewModels
        }
    }

    @ExperimentalFoundationApi
    @Composable
    private fun ActivityScreenForHost() {
        val screen = dashboardViewModel.screen.observeAsState().value
        val connectionStatus = connectionHostViewModel.connectionStatus.observeAsState().value
        MaterialTheme {
            Surface(color = Color.White) {
                GameRoomHostScreen(
                    screen = screen,
                    onCardClick = dashboardViewModel::playCard,
                    onPassClick = dashboardViewModel::passTurn,
                    onAddCardToExchange = dashboardViewModel::addCardToExchange,
                    onRemoveCardFromExchange = dashboardViewModel::removeCardFromExchange,
                    onConfirmExchange = dashboardViewModel::confirmExchange,
                    onStartNewRoundClick = hostViewModel::startNewRound,
                    connectionStatus = connectionStatus,
                    onEndGameClick = hostViewModel::endGame,
                    onReconnectClick = connectionHostViewModel::reconnect
                )
            }
        }
    }

    @ExperimentalFoundationApi
    @Composable
    private fun ActivityScreenForGuest() {
        val communicationState = connectionGuestViewModel.communicationState.observeAsState().value
        val screen = dashboardViewModel.screen.observeAsState().value
        val gameOver = guestViewModel.gameOver.observeAsState(false).value
        MaterialTheme {
            Surface(color = Color.White) {
                GameRoomGuestScreen(
                    screen = screen,
                    showGameOver = gameOver,
                    onCardClick = dashboardViewModel::playCard,
                    onPassClick = dashboardViewModel::passTurn,
                    onAddCardToExchange = dashboardViewModel::addCardToExchange,
                    onRemoveCardFromExchange = dashboardViewModel::removeCardFromExchange,
                    onConfirmExchange = dashboardViewModel::confirmExchange,
                    connectionStatus = communicationState,
                    onReconnectClick = connectionGuestViewModel::reconnect,
                    onGameOverAcknowledge = { onGameOverAcknowledge() },
                    onLeaveGameClick = { onLeaveGameClick() }
                )
            }
        }
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        playerRole = PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE)!!)

        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(GameRoomViewModel::class.java)
        dashboardViewModel = viewModelProvider.get(PlayerDashboardViewModel::class.java)

        when (playerRole) {
            PlayerRole.HOST -> {
                hostViewModel = viewModelProvider.get(GameRoomHostViewModel::class.java)
                connectionHostViewModel = viewModelProvider.get(ConnectionHostViewModel::class.java)
                setupHostCommands()
                setContent { ActivityScreenForHost() }
            }
            PlayerRole.GUEST -> {
                guestViewModel = viewModelProvider.get(GameRoomGuestViewModel::class.java)
                connectionGuestViewModel = viewModelProvider.get(ConnectionGuestViewModel::class.java)
                setupGuestCommands()
                setContent { ActivityScreenForGuest() }
            }
        }
    }

    private fun setupHostCommands() {
        hostViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    GameRoomHostCommand.NavigateToHomeScreen -> {
                        MainActivity.start(this)
                        finish()
                    }
                }
            }
        )
    }

    private fun setupGuestCommands() {
        guestViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    GameRoomGuestCommand.NavigateToHomeScreen -> {
                        MainActivity.start(this)
                        finish()
                    }
                }
            }
        )
    }

    private fun onGameOverAcknowledge() {
        guestViewModel.acknowledgeGameOver()
    }

    private fun onLeaveGameClick() {
        guestViewModel.leaveGame()
    }

    override fun onStart() {
        super.onStart()
        relevantViewModels.forEach { vm -> vm.onStart() }
    }

    override fun onStop() {
        super.onStop()
        relevantViewModels.forEach { vm -> vm.onStop() }
    }

    companion object {
        fun startForHost(context: Context) {
            startActivity(context, PlayerRole.HOST)
        }

        fun startForGuest(context: Context) {
            startActivity(context, PlayerRole.GUEST)
        }

        private fun startActivity(context: Context, playerRole: PlayerRole) {
            val intent = Intent(context, GameRoomActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, playerRole.name)
            context.startActivity(intent)
        }
    }
}
