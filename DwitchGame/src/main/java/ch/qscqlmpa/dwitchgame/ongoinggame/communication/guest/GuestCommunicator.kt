package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import io.reactivex.rxjava3.core.Observable

internal interface GuestCommunicator : GameCommunicator {

    fun currentCommunicationState(): Observable<GuestCommunicationState>

    fun connect()

    fun closeConnection()
}