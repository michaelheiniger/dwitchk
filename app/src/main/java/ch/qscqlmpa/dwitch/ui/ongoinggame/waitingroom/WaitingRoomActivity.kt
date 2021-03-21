package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.databinding.ActivityWaitingRoomBinding
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.playerlist.PlayerWrAdapter
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class WaitingRoomActivity : OngoingGameBaseActivity<WaitingRoomViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setFragmentForRole(PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE)!!))
        }

        val binding = ActivityWaitingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory).get(WaitingRoomViewModel::class.java)

        binding.playerListRw.layoutManager = LinearLayoutManager(this)
        binding.playerListRw.adapter = PlayerWrAdapter()

        viewModel.playersInWaitingRoom().observe(
            this, { list -> (binding.playerListRw.adapter as PlayerWrAdapter).setData(list) }
        )
    }

    private fun setFragmentForRole(playerRole: PlayerRole) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (playerRole) {
                PlayerRole.GUEST -> add<WaitingRoomGuestFragment>(R.id.wra_host_or_guest_fragment_container)
                PlayerRole.HOST -> add<WaitingRoomHostFragment>(R.id.wra_host_or_guest_fragment_container)
            }
        }
    }

    companion object {

        fun startActivityForHost(context: Context) {
            val intent = Intent(context, WaitingRoomActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Finishes all Activities in the backstack
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.HOST.name)
            context.startActivity(intent)
        }

        fun startActivityForGuest(context: Context) {
            val intent = Intent(context, WaitingRoomActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK) // Finishes all Activities in the backstack
            intent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.GUEST.name)
            context.startActivity(intent)
        }
    }
}
