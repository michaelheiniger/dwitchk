package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment

class GameRoomHostFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.game_room_host_fragment

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    companion object {

        fun create(): GameRoomHostFragment {
            return GameRoomHostFragment()
        }
    }
}
