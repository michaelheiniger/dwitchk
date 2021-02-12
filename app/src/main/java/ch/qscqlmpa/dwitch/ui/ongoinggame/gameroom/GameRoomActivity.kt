package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomGuestFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host.GameRoomHostFragment
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.PlayerDashboardFragment
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

class GameRoomActivity : OngoingGameBaseActivity() {

    override val layoutResource: Int = R.layout.game_room_activity

    private lateinit var viewModel: GameRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).getGameUiComponent()!!.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(GameRoomViewModel::class.java)

        setFragments(PlayerRole.valueOf(intent.getStringExtra(EXTRA_PLAYER_ROLE)))
    }

    private fun setFragments(playerRole: PlayerRole) {
        val controlFragment = when (playerRole) {
            PlayerRole.GUEST -> GameRoomGuestFragment.create()
            PlayerRole.HOST -> GameRoomHostFragment.create()
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.host_or_guest_fragment_container, controlFragment)
            .add(R.id.game_dashboard_fragment_container, PlayerDashboardFragment.create())
            .commit()
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
