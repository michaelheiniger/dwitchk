package ch.qscqlmpa.dwitch.ui

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

object PlayerConnectionStateTextMapper {

    fun resource(state: PlayerConnectionState): Int {
        return when (state) {
            PlayerConnectionState.CONNECTED -> R.string.player_connected
            PlayerConnectionState.DISCONNECTED -> R.string.player_disconnected
        }
    }
}