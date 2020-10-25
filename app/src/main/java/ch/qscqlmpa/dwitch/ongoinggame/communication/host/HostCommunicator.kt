package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import io.reactivex.Observable

interface HostCommunicator : GameCommunicator {

    fun listenForConnections()

    fun observeCommunicationState(): Observable<HostCommunicationState>

    fun closeAllConnections()

    fun closeConnectionWithClient(localConnectionId: LocalConnectionId)
}