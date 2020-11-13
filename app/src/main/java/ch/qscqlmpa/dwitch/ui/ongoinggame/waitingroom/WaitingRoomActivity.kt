package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest.WaitingRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host.WaitingRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.playerlist.PlayerWrAdapter
import kotlinx.android.synthetic.main.waiting_room_activity.*

class WaitingRoomActivity : OngoingGameBaseActivity() {

    override val layoutResource: Int = R.layout.waiting_room_activity

    private lateinit var viewModel: WaitingRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(WaitingRoomViewModel::class.java)

        when (PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE))) {
            PlayerRole.GUEST -> setControlFragment(WaitingRoomGuestFragment.create())
            PlayerRole.HOST -> setControlFragment(WaitingRoomHostFragment.create())
        }

        playerListRw.layoutManager = LinearLayoutManager(this)
        playerListRw.adapter = PlayerWrAdapter()

        viewModel.playersInWaitingRoom().observe(this,
            { list -> (playerListRw.adapter as PlayerWrAdapter).setData(list) })

    }

    private fun setControlFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.host_or_guest_fragment_container, fragment)
                .commit()
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
