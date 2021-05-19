package ch.qscqlmpa.dwitch.ui.home.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameActivity
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity

class MainActivity : HomeBaseActivity() {

    private lateinit var wrViewModel: MainActivityViewModel

    @Composable
    fun ActivityScreen(viewModel: MainActivityViewModel) {
        val advertisedGames = viewModel.advertisedGames.observeAsState(LoadedData.Loading)
        val resumableGames = viewModel.resumableGames.observeAsState(LoadedData.Loading)
        HomeScreen(
            advertisedGames = advertisedGames.value,
            resumableGames = resumableGames.value,
            onJoinGameClick = { game -> viewModel.joinGame(game) },
            onCreateNewGameClick = { HostNewGameActivity.hostNewGame(this) },
            onResumableGameClick = { game -> viewModel.resumeGame(game) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wrViewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        setContent { ActivityScreenContainer { ActivityScreen(wrViewModel) } }

        wrViewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    is MainActivityCommands.NavigateToNewGameActivityAsGuest -> JoinNewGameActivity.joinGame(this, command.game)
                    MainActivityCommands.NavigateToWaitingRoomAsGuest -> {
                        finish()
                        WaitingRoomActivity.startActivityForGuest(this)
                    }
                    MainActivityCommands.NavigateToWaitingRoomAsHost -> {
                        finish()
                        WaitingRoomActivity.startActivityForHost(this)
                    }
                    MainActivityCommands.NavigateToGameRoomAsGuest -> {
                        finish()
                        GameRoomActivity.startForGuest(this)
                    }
                    MainActivityCommands.NavigateToGameRoomAsHost -> {
                        finish()
                        GameRoomActivity.startForHost(this)
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
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
