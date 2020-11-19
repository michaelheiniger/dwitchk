package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import io.reactivex.Observable

interface GuestCommunicator : GameCommunicator {

    fun observeCommunicationState(): Observable<GuestCommunicationState>

    fun connect()

    fun closeConnection()
}