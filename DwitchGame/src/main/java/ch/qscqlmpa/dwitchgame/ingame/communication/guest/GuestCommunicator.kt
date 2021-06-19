package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator

internal interface GuestCommunicator : GameCommunicator {

    fun connect()

    fun disconnect()
}
