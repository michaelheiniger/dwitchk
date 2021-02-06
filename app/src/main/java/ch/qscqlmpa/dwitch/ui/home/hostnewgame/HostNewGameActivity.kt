package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.host_new_game_activity.*
import rx.android.BuildConfig
import rx.subscriptions.CompositeSubscription

class HostNewGameActivity : HomeBaseActivity() {

    override val layoutResource: Int = R.layout.host_new_game_activity

    private lateinit var viewModel: HostNewGameViewModel

    private val subscriptions = CompositeSubscription()

    fun onHostNewGameClick(@Suppress("UNUSED_PARAMETER") view: View) {
        viewModel.hostGame()
    }

    fun onBackClick(@Suppress("UNUSED_PARAMETER") view: View) {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HostNewGameViewModel::class.java)

        setupPlayerNameEdt()
        setupGameNameEdt()

        observeHostGameControlState()
        observeCommands()
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            RxTextView.textChanges(playerNameEdt).subscribe { value -> viewModel.onPlayerNameChange(value.toString()) }
        )

        subscriptions.add(
            RxTextView.textChanges(gameNameEdt).subscribe { value -> viewModel.onGameNameChange(value.toString()) }
        )
    }

    override fun onStop() {
        super.onStop()
        subscriptions.clear()
    }

    private fun observeCommands() {
        viewModel.observeCommands().observe(
            this,
            { event ->
                when (event) {
                    HostNewGameCommand.NavigateToWaitingRoom -> WaitingRoomActivity.startActivityForHost(this)
                    else -> {
                    } // Nothing to do
                }
            }
        )
    }

    private fun observeHostGameControlState() {
        viewModel.observeHostGameControleState().observe(this, { ctrlState -> hostGameBtn.isEnabled = ctrlState.enabled })
    }

    @SuppressLint("SetTextI18n")
    private fun setupPlayerNameEdt() {
        if (BuildConfig.DEBUG) {
            playerNameEdt.setText("Mirlick")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupGameNameEdt() {
        if (BuildConfig.DEBUG) {
            gameNameEdt.setText("Dwiiitch !")
        }
    }

    companion object {

        fun hostNewGame(context: Context) {
            val intent = Intent(context, HostNewGameActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.HOST.name)
            context.startActivity(intent)
        }
    }
}
