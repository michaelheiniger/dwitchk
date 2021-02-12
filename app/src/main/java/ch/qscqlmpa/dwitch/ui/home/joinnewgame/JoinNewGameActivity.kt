package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.join_new_game_activity.*
import rx.subscriptions.CompositeSubscription
import java.util.*

class JoinNewGameActivity : HomeBaseActivity() {

    override val layoutResource: Int = R.layout.join_new_game_activity

    private lateinit var viewModel: JoinNewGameViewModel
    private var game: AdvertisedGame? = null

    private val subscriptions = CompositeSubscription()

    fun onJoiNnewGameClick(@Suppress("UNUSED_PARAMETER") view: View) {
        viewModel.joinGame(game!!)
    }

    fun onBackClick(@Suppress("UNUSED_PARAMETER") view: View) {
        finish()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        game = intent.getParcelableExtra(EXTRA_GAME)
        viewModel = ViewModelProvider(this, viewModelFactory).get(JoinNewGameViewModel::class.java)

        setupPlayerNameEdt()

        observeJoinGameControlState()
        observeCommands()
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            RxTextView.textChanges(playerNameEdt).subscribe { value -> viewModel.onPlayerNameChange(value.toString()) }
        )
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }

    @SuppressLint("SetTextI18n")
    private fun setupPlayerNameEdt() {
        if (BuildConfig.DEBUG) {
            playerNameEdt.setText("Mébène")
        }
    }

    private fun observeCommands() {
        viewModel.observeCommands().observe(
            this,
            { event ->
                when (event) {
                    JoinNewGameCommand.NavigateToWaitingRoom -> WaitingRoomActivity.startActivityForGuest(this)
                    else -> {
                    } // Nothing to do
                }
            }
        )
    }

    private fun observeJoinGameControlState() {
        viewModel.observeJoinGameControlState().observe(this, { nextControl -> joinGameBtn.isEnabled = nextControl.enabled })
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
