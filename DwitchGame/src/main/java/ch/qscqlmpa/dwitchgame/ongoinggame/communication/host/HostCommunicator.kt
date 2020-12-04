package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import io.reactivex.rxjava3.core.Observable

interface HostCommunicator : GameCommunicator {

    fun listenForConnections()

    fun observeCommunicationState(): Observable<HostCommunicationState>

    fun closeAllConnections()

    fun closeConnectionWithClient(connectionId: ConnectionId)
}