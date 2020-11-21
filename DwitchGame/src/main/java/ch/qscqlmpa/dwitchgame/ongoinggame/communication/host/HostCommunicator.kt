package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import io.reactivex.Observable

interface HostCommunicator : GameCommunicator {

    fun listenForConnections()

    fun observeCommunicationState(): Observable<HostCommunicationState>

    fun closeAllConnections()

    fun closeConnectionWithClient(localConnectionId: LocalConnectionId)
}