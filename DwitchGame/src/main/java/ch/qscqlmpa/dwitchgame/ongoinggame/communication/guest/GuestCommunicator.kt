package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import io.reactivex.rxjava3.core.Observable

internal interface GuestCommunicator : GameCommunicator {

    fun connect()

    fun disconnect()

    fun currentCommunicationState(): Observable<GuestCommunicationState>
}
