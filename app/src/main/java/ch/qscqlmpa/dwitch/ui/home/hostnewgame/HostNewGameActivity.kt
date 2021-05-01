package ch.qscqlmpa.dwitch.ui.home.hostnewgame

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
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.common.LoadingDialog
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class HostNewGameActivity : HomeBaseActivity() {

    private lateinit var wrViewModel: HostNewGameViewModel
    private val initialPlayerName = if (BuildConfig.DEBUG) "Mirlick" else ""
    private val initialGameName = if (BuildConfig.DEBUG) "Dwiiitch !" else ""
    private val initialHostGameControl = BuildConfig.DEBUG

    @Composable
    fun ActivityScreen(viewModel: HostNewGameViewModel) {
        val playerName = viewModel.playerName.observeAsState(initialPlayerName).value
        val gameName = viewModel.gameName.observeAsState(initialGameName).value
        val hostGameControl = viewModel.createGameControl.observeAsState(initialHostGameControl).value
        MaterialTheme {
            Surface(color = Color.White) {
                HostNewGameScreen(
                    playerName = playerName,
                    gameName = gameName,
                    hostGameControlEnabled = hostGameControl,
                    onPlayerNameChange = { name -> viewModel.onPlayerNameChange(name) },
                    onGameNameChange = { name -> viewModel.onGameNameChange(name) },
                    onCreateGameClick = { viewModel.hostGame() },
                    onBackClick = {
                        MainActivity.start(this)
                        finish()
                    }
                )
                if (viewModel.loading.observeAsState(false).value) LoadingDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wrViewModel = ViewModelProvider(this, viewModelFactory).get(HostNewGameViewModel::class.java)
        setContent { ActivityScreen(wrViewModel) }
        observeNavigationCommands()
    }

    private fun observeNavigationCommands() {
        wrViewModel.navigationCommand.observe(
            this,
            { event ->
                when (event) {
                    HostNewGameNavigationCommand.NavigateToWaitingRoom -> {
                        finish()
                        WaitingRoomActivity.startActivityForHost(this)
                    }
                    else -> {
                    } // Nothing to do
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        wrViewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        wrViewModel.onStop()
    }

    companion object {
        fun hostNewGame(context: Context) {
            val intent = Intent(context, HostNewGameActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.HOST.name)
            context.startActivity(intent)
        }
    }
}
