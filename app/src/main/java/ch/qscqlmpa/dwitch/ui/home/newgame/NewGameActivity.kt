package ch.qscqlmpa.dwitch.ui.home.newgame

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import kotlinx.android.synthetic.main.new_game_activity.*
import timber.log.Timber
import java.util.*

class NewGameActivity : HomeBaseActivity() {

    override val layoutResource: Int = R.layout.new_game_activity

    private lateinit var viewModel: NewGameActivityViewModel
    private lateinit var playerRole: PlayerRole
    private var game: AdvertisedGame? = null

    fun onNextClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        val playerName = playerNameEdt.text.toString()
        val gameName = gameNameEdt.text.toString()
        when (playerRole) {
            PlayerRole.GUEST -> viewModel.nextForGuest(game!!, playerName)
            PlayerRole.HOST -> viewModel.nextForHost(gameName, playerName)
        }

    }

    fun onBackClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        Timber.i("Back to home screen.")
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerRole = PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE))
        game = intent.getParcelableExtra(EXTRA_GAME)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewGameActivityViewModel::class.java)

        setGameNameIfNeeded()

        if (BuildConfig.DEBUG) {
            when (playerRole) {
                PlayerRole.GUEST -> playerNameEdt.setText("Mébène")
                PlayerRole.HOST -> playerNameEdt.setText("Mirlick")
            }
        }

        observeErrors()
        observeEvents()
    }

    private fun observeEvents() {
        viewModel.observeEvents().observe(this, Observer { event ->
            when (event) {
                NewGameEvent.SETUP_SUCCESSFUL -> startWaitingRoomActivity()
                else -> {
                } // Nothing to do
            }
        })
    }

    private fun startWaitingRoomActivity() {
        when (playerRole) {
            PlayerRole.GUEST -> WaitingRoomActivity.startActivityForGuest(this)
            PlayerRole.HOST -> WaitingRoomActivity.startActivityForHost(this)
        }
    }

    private fun observeErrors() {
        viewModel.observeErrors().observe(this, Observer { newGameErrors ->

            for (error in newGameErrors) {

                when (error) {

                    NewGameError.SETUP_HOST_ERROR -> {//TODO
                    }
                    NewGameError.CONNECTION_TO_HOST_ERROR -> {//TODO
                    }
                    NewGameError.PLAYER_NAME_IS_EMPTY -> playerNameEdt.error = getString(error.ressourceId)
                    NewGameError.GAME_NAME_IS_EMPTY -> gameNameEdt.error = getString(error.ressourceId)
                }
            }
        })
    }

    private fun setGameNameIfNeeded() {
        when (playerRole) {
            PlayerRole.GUEST -> {
                gameNameEdt.isEnabled = false
                gameNameEdt.setText(game!!.gameName)
            }
            PlayerRole.HOST -> {
                gameNameEdt.isEnabled = true
                if (BuildConfig.DEBUG) {
                    gameNameEdt.setText("Dwiiitch !")
                }
            }
        }
    }

    companion object {

        private const val EXTRA_GAME = "new_game"

        fun joinGame(context: Context, game: AdvertisedGame) {
            Objects.requireNonNull(game)
            val intent = Intent(context, NewGameActivity::class.java)
            intent.putExtra(EXTRA_GAME, game)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.GUEST.name)
            context.startActivity(intent)
        }

        fun createGame(context: Context) {
            val intent = Intent(context, NewGameActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.HOST.name)
            context.startActivity(intent)
        }
    }
}