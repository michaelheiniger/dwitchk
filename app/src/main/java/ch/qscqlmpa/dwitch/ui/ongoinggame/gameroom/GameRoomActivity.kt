package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.databinding.ActivityGameRoomBinding
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostFragment
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class GameRoomActivity : OngoingGameBaseActivity() {

    private lateinit var wrViewModel: GameRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setFragmentForRole(PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE)!!))
        }

        val binding = ActivityGameRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wrViewModel = ViewModelProvider(this, viewModelFactory).get(GameRoomViewModel::class.java)
    }

    private fun setFragmentForRole(playerRole: PlayerRole) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (playerRole) {
                PlayerRole.GUEST -> add<GameRoomGuestFragment>(R.id.gra_host_or_guest_fragment_container)
                PlayerRole.HOST -> add<GameRoomHostFragment>(R.id.gra_host_or_guest_fragment_container)
            }
        }
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
        fun startForHost(context: Context) {
            start(context, PlayerRole.HOST)
        }

        fun startForGuest(context: Context) {
            start(context, PlayerRole.GUEST)
        }

        private fun start(context: Context, playerRole: PlayerRole) {
            val intent = Intent(context, GameRoomActivity::class.java)
            intent.putExtra(EXTRA_PLAYER_ROLE, playerRole.name)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Finishes all Activities in the backstack
            context.startActivity(intent)
        }
    }
}
