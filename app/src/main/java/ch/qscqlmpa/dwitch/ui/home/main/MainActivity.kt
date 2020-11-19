package ch.qscqlmpa.dwitch.ui.home.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameActivity
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber

class MainActivity : HomeBaseActivity(), GameAdapter.GameClickedListener {

    override val layoutResource: Int = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameListRw.layoutManager = LinearLayoutManager(this)
        gameListRw.adapter = GameAdapter(this)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)
        viewModel.observeAdvertisedGames().observe(this, { response ->
            when (response.status) {
                Status.LOADING -> { // Nothing to do
                }
                Status.SUCCESS -> (gameListRw.adapter as GameAdapter).setData(response.advertisedGames)
                Status.ERROR -> {
                    gameListErrorTv.setText(R.string.ma_advertised_games_error_tv)
                    Timber.d(response.error, "Error while observing advertised games.")
                }
            }
        })
    }

    override fun onGameClicked(advertisedGame: AdvertisedGame) {
        NewGameActivity.joinGame(this, advertisedGame)
    }

    fun onCreateClicked(@Suppress("UNUSED_PARAMETER") view: View) {
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
