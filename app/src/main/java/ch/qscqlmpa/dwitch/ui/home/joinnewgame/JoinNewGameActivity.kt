package ch.qscqlmpa.dwitch.ui.home.joinnewgame

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
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import java.util.*

class JoinNewGameActivity : HomeBaseActivity() {

    private lateinit var wrViewModel: JoinNewGameViewModel
    private lateinit var game: AdvertisedGame

    private val initialPlayerName = if (BuildConfig.DEBUG) "Mébène" else ""
    private val initialJoinGameControl = BuildConfig.DEBUG

    @Composable
    fun ActivityScreen(viewModel: JoinNewGameViewModel) {
        val playerName = viewModel.playerName.observeAsState(initialPlayerName).value
        val joinGameControl = viewModel.joinGameControl.observeAsState(initialJoinGameControl).value
        val command = viewModel.commands.observeAsState().value
        MaterialTheme {
            Surface(color = Color.White) {
                JoinNewGameScreen(
                    gameName = game.gameName,
                    playerName = playerName,
                    joinGameControlEnabled = joinGameControl,
                    onPlayerNameChange = { name -> viewModel.onPlayerNameChange(name) },
                    onJoinGameClick = { viewModel.joinGame(game) },
                    onBackClick = {
                        finish()
                        MainActivity.start(this)
                    }
                )
                if (command != null && command is JoinNewGameCommand.Loading) LoadingDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        game = intent.getParcelableExtra(EXTRA_GAME)
        wrViewModel = ViewModelProvider(this, viewModelFactory).get(JoinNewGameViewModel::class.java)
        setContent { ActivityScreen(wrViewModel) }
        observeCommands()
    }

    private fun observeCommands() {
        wrViewModel.commands.observe(
            this,
            { event ->
                when (event) {
                    JoinNewGameCommand.NavigateToWaitingRoom -> {
                        finish()
                        WaitingRoomActivity.startActivityForGuest(this)
                    }
                    else -> {
                        // Nothing to do
                    }
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

        private const val EXTRA_GAME = "new_game"

        fun joinGame(context: Context, game: AdvertisedGame) {
            Objects.requireNonNull(game)
            val intent = Intent(context, JoinNewGameActivity::class.java)
            intent.putExtra(EXTRA_GAME, game)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.GUEST.name)
            context.startActivity(intent)
        }
    }
}
