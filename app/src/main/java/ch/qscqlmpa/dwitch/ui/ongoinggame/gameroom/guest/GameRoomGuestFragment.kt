package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.ui.ongoinggame.OngoingGameBaseFragment

class GameRoomGuestFragment : OngoingGameBaseFragment() {

    override val layoutResource: Int = R.layout.game_room_guest_fragment

    override fun inject() {
        (activity!!.application as App).getGameComponent()!!.inject(this)
    }

    companion object {

        fun create(): GameRoomGuestFragment {
            return GameRoomGuestFragment()
        }
    }
}
