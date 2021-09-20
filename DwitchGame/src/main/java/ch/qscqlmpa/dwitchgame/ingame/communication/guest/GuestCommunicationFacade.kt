package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import io.reactivex.rxjava3.core.Observable

interface GuestCommunicationFacade {

    /**
     * Connects to the server using initial advertised game.
     */
    fun connect()

    /**
     * Connects to the server using provided advertised game.
     */
    fun connect(advertisedGame: AdvertisedGame)

    /**
     * Disconnects from the server.
     */
    fun disconnect()

    /**
     * Emits the current communication state of the guest.
     */
    fun currentCommunicationState(): Observable<GuestCommunicationState>
}
