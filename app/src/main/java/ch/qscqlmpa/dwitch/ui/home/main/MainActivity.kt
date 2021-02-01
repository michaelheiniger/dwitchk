package ch.qscqlmpa.dwitch.ui.home.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber

class MainActivity : HomeBaseActivity(), AdvertisedGameAdapter.AdvertisedGameClickedListener, ExistingGameAdapter.ExistingGameClickedListener {

    override val layoutResource: Int = R.layout.main_activity

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)

        viewModel.commands().observe(this, { command ->
            Timber.d("Command: $command")
            when (command) {
                is MainActivityCommands.NavigateToNewGameActivityAsGuest -> NewGameActivity.joinGame(this, command.game)
                MainActivityCommands.NavigateToWaitingRoomAsHost -> WaitingRoomActivity.startActivityForHost(this)
                MainActivityCommands.NavigateToWaitingRoomAsGuest -> WaitingRoomActivity.startActivityForGuest(this)
            }
        })

        gameListRw.layoutManager = LinearLayoutManager(this)
        gameListRw.adapter = AdvertisedGameAdapter(this)
        viewModel.observeAdvertisedGames().observe(this, { response ->
            when (response.status) {
                Status.LOADING -> { // Nothing to do
                }
                Status.SUCCESS -> (gameListRw.adapter as AdvertisedGameAdapter).setData(response.advertisedGames)
                Status.ERROR -> {
                    gameListErrorTv.visibility = View.VISIBLE
                    Timber.e(response.error, "Error while observing advertised games.")
                }
            }
        })

        existingGameListRw.layoutManager = LinearLayoutManager(this)
        existingGameListRw.adapter = ExistingGameAdapter(this)
        viewModel.observeExistingGames().observe(this, { response ->
            when (response.status) {
                Status.LOADING -> { // Nothing to do
                }
                Status.SUCCESS -> (existingGameListRw.adapter as ExistingGameAdapter).setData(response.resumableGames)
                Status.ERROR -> {
                    existingGameListErrorTv.visibility = View.VISIBLE
                    Timber.d(response.error, "Error while observing advertised games.")
                }
            }
        })
    }

    override fun onGameClicked(selectedGame: AdvertisedGame) {
        viewModel.joinGame(selectedGame)
    }

    override fun onGameClicked(selectedGame: ResumableGameInfo) {
        viewModel.resumeGame(selectedGame)
    }

    fun onCreateGameClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        NewGameActivity.createGame(this)
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Finishes all Activities in the backstack
            context.startActivity(intent)
        }
    }
}
