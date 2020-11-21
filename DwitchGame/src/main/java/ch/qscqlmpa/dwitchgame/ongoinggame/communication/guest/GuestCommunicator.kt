package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import io.reactivex.Observable

interface GuestCommunicator : GameCommunicator {

    fun observeCommunicationState(): Observable<GuestCommunicationState>

    fun connect()

    fun closeConnection()
}