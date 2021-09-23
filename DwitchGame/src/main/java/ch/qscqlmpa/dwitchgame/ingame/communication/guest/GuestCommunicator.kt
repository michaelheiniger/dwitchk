package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator

internal interface GuestCommunicator : GameCommunicator {

    /**
     * Use the provided avdertised game.
     */
    fun connect(advertisedGame: GameAdvertisingInfo)

    /**
     * Use the initial advertised game.
     */
    fun connect()

    fun disconnect()
}
