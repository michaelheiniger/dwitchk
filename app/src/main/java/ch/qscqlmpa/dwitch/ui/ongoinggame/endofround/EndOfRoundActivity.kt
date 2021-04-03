package ch.qscqlmpa.dwitch.ui.ongoinggame.endofround

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
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class EndOfRoundActivity : OngoingGameBaseActivity() {

    private lateinit var viewModel: EndOfRoundViewModel
    private lateinit var hostViewModel: EndOfRoundHostViewModel
    private lateinit var guestViewModel: EndOfRoundGuestViewModel
    private lateinit var playerRole: PlayerRole

    private val hostViewModels: List<BaseViewModel> by lazy {
        listOf(viewModel, hostViewModel)
    }

    private val guestViewModels: List<BaseViewModel> by lazy {
        listOf(viewModel, guestViewModel)
    }

    private val relevantViewModels: List<BaseViewModel> by lazy {
        when (playerRole) {
            PlayerRole.HOST -> {
                hostViewModels
            }
            PlayerRole.GUEST -> {
                guestViewModels
            }
        }
    }

    @Composable
    private fun ActivityScreenForHost() {
        MaterialTheme {
            Surface(color = Color.White) {
                EndOfRoundScreen(
                    viewModel.endOfRoundInfo.observeAsState().value,
                    onStartNewRoundClick = hostViewModel::startNewRound,
                    onGameOverClick = hostViewModel::endGame,
                    onLeaveGameClick = {} // Only for Guests
                )
            }
        }
    }

    @Composable
    private fun ActivityScreenForGuest() {
        MaterialTheme {
            Surface(color = Color.White) {
                EndOfRoundScreen(
                    viewModel.endOfRoundInfo.observeAsState().value,
                    onStartNewRoundClick = {}, // Only for Hosts
                    onGameOverClick = {}, // Only for Hosts
                    onLeaveGameClick = guestViewModel::leaveGame
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        playerRole = PlayerRole.valueOf(intent.getStringExtra(CommonExtraConstants.EXTRA_PLAYER_ROLE)!!)

        val viewModelProvider = ViewModelProvider(this, viewModelFactory)
        viewModel = viewModelProvider.get(EndOfRoundViewModel::class.java)

        when (playerRole) {
            PlayerRole.HOST -> {
                hostViewModel = viewModelProvider.get(EndOfRoundHostViewModel::class.java)
                setupHostCommands()
                setContent { ActivityScreenForHost() }
            }
            PlayerRole.GUEST -> {
                guestViewModel = viewModelProvider.get(EndOfRoundGuestViewModel::class.java)
                setupGuestCommands()
                setContent { ActivityScreenForGuest() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        relevantViewModels.forEach { vm -> vm.onStart() }
    }

    override fun onStop() {
        super.onStop()
        relevantViewModels.forEach { vm -> vm.onStop() }
    }

    private fun setupHostCommands() {
        hostViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    EndOfRoundHostCommand.NavigateHome -> {
                        MainActivity.start(this)
                        finish()
                    }
                    EndOfRoundHostCommand.NavigateToGameRoom -> finish()
                }
            }
        )
    }

    private fun setupGuestCommands() {
        guestViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    EndOfRoundGuestCommand.NavigateHome -> MainActivity.start(this)
                }
            }
        )
    }

    companion object {
        fun startForHost(context: Context) {
            startActivity(context, PlayerRole.HOST)
        }

        fun startForGuest(context: Context) {
            startActivity(context, PlayerRole.GUEST)
        }

        private fun startActivity(context: Context, playerRole: PlayerRole) {
            val intent = Intent(context, EndOfRoundActivity::class.java)
            intent.putExtra(CommonExtraConstants.EXTRA_PLAYER_ROLE, playerRole.name)
            context.startActivity(intent)
        }
    }
}
