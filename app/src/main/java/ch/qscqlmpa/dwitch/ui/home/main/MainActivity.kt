package ch.qscqlmpa.dwitch.ui.home.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ch.qscqlmpa.dwitch.databinding.ActivityMainBinding
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameActivity
import ch.qscqlmpa.dwitch.ui.home.joinnewgame.JoinNewGameActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import mu.KLogging

class MainActivity : HomeBaseActivity(), AdvertisedGameAdapter.AdvertisedGameClickedListener, ExistingGameAdapter.ExistingGameClickedListener {

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        viewModel.commands().observe(
            this,
            { command ->
                when (command) {
                    is MainActivityCommands.NavigateToNewGameActivityAsGuest -> JoinNewGameActivity.joinGame(this, command.game)
                    MainActivityCommands.NavigateToWaitingRoomAsHost -> WaitingRoomActivity.startActivityForHost(this)
                    MainActivityCommands.NavigateToWaitingRoomAsGuest -> WaitingRoomActivity.startActivityForGuest(this)
                }
            }
        )

        binding.gameListRw.layoutManager = LinearLayoutManager(this)
        binding.gameListRw.adapter = AdvertisedGameAdapter(this)
        viewModel.observeAdvertisedGames().observe(
            this,
            { response ->
                when (response.status) {
                    Status.LOADING -> { // Nothing to do
                    }
                    Status.SUCCESS -> (binding.gameListRw.adapter as AdvertisedGameAdapter).setData(response.advertisedGames)
                    Status.ERROR -> {
                        binding.gameListErrorTv.visibility = View.VISIBLE
                        logger.error(response.error) { "Error while observing advertised games." }
                    }
                }
            }
        )

        binding.existingGameListRw.layoutManager = LinearLayoutManager(this)
        binding.existingGameListRw.adapter = ExistingGameAdapter(this)
        viewModel.observeExistingGames().observe(
            this,
            { response ->
                when (response.status) {
                    Status.LOADING -> { // Nothing to do
                    }
                    Status.SUCCESS -> (binding.existingGameListRw.adapter as ExistingGameAdapter).setData(response.resumableGames)
                    Status.ERROR -> {
                        binding.existingGameListErrorTv.visibility = View.VISIBLE
                        logger.error(response.error) { "Error while observing advertised games." }
                    }
                }
            }
        )
    }

    override fun onGameClicked(selectedGame: AdvertisedGame) {
        viewModel.joinGame(selectedGame)
    }

    override fun onGameClicked(selectedGame: ResumableGameInfo) {
        viewModel.resumeGame(selectedGame)
    }

    fun onCreateGameClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        HostNewGameActivity.hostNewGame(this)
    }

    companion object : KLogging() {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Finishes all Activities in the backstack
            context.startActivity(intent)
        }
    }
}
