package ch.qscqlmpa.dwitch.ui.home.main

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
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameActivity
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import mu.KLogging

class MainActivity : HomeBaseActivity<MainActivityViewModel>() {

    @Composable
    fun ActivityScreen(viewModel: MainActivityViewModel) {
        MaterialTheme {
            Surface(color = Color.White) {
                val advertisedGameResponse =
                    viewModel.advertisedGames.observeAsState(AdvertisedGameResponse(Status.LOADING, emptyList()))
                val resumableGameResponse =
                    viewModel.resumableGames.observeAsState(initial = ResumableGameResponse(Status.LOADING, emptyList()))
                HomeScreen(
                    advertisedGameResponse = advertisedGameResponse.value,
                    resumableGameResponse = resumableGameResponse.value,
                    onJoinGameClick = { game -> viewModel.joinGame(game) },
                    onCreateNewGameClick = { HostNewGameActivity.hostNewGame(this) },
                    onResumableGameClick = { game -> viewModel.resumeGame(game) }
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        setContent {
            ActivityScreen(viewModel)
        }

        viewModel.commands.observe(
            this,
            { command ->
                when (command) {
                    is MainActivityCommands.NavigateToNewGameActivityAsGuest -> JoinNewGameActivity.joinGame(this, command.game)
                    MainActivityCommands.NavigateToWaitingRoomAsGuest -> WaitingRoomActivity.startActivityForGuest(this)
                    MainActivityCommands.NavigateToWaitingRoomAsHost -> WaitingRoomActivity.startActivityForHost(this)
                    MainActivityCommands.NavigateToGameRoomAsGuest -> GameRoomActivity.startForGuest(this)
                    MainActivityCommands.NavigateToGameRoomAsHost -> GameRoomActivity.startForHost(this)
                }
            }
        )
    }

    companion object : KLogging() {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Finishes all Activities in the backstack
            context.startActivity(intent)
        }
    }
}
