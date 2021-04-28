package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator

internal interface GuestCommunicator : GameCommunicator {

    fun connect()

    fun disconnect()
}
