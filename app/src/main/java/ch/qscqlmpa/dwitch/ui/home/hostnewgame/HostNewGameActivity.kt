package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.databinding.ActivityHostNewGameBinding
import ch.qscqlmpa.dwitch.ui.home.HomeBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import com.jakewharton.rxbinding.widget.RxTextView
import rx.subscriptions.CompositeSubscription

class HostNewGameActivity : HomeBaseActivity<HostNewGameViewModel>() {

    private val subscriptions = CompositeSubscription()

    private lateinit var binding: ActivityHostNewGameBinding

    fun onHostNewGameClick(@Suppress("UNUSED_PARAMETER") view: View) {
        viewModel.hostGame()
    }

    fun onBackClick(@Suppress("UNUSED_PARAMETER") view: View) {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostNewGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory).get(HostNewGameViewModel::class.java)

        setupPlayerNameEdt()
        setupGameNameEdt()

        observeHostGameControlState()
        observeCommands()
    }

    override fun onStart() {
        super.onStart()
        subscriptions.add(
            RxTextView.textChanges(binding.playerNameEdt).subscribe { value -> viewModel.onPlayerNameChange(value.toString()) }
        )

        subscriptions.add(
            RxTextView.textChanges(binding.gameNameEdt).subscribe { value -> viewModel.onGameNameChange(value.toString()) }
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
        viewModel.observeHostGameControleState().observe(this, { ctrlState -> binding.hostGameBtn.isEnabled = ctrlState.enabled })
    }

    @SuppressLint("SetTextI18n")
    private fun setupPlayerNameEdt() {
        if (BuildConfig.DEBUG) {
            binding.playerNameEdt.setText("Mirlick")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupGameNameEdt() {
        if (BuildConfig.DEBUG) {
            binding.gameNameEdt.setText("Dwiiitch !")
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
