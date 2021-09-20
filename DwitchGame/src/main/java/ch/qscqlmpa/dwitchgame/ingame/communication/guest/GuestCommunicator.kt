package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator

internal interface GuestCommunicator : GameCommunicator {

    /**
     * Use the provided avdertised game.
     */
    fun connect(advertisedGame: AdvertisedGame)

    /**
     * Use the initial advertised game.
     */
    fun connect()

    fun disconnect()
}
