package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Completable
import io.reactivex.Observable

/**
 *
 * Note: Interface used instead of a class because Mockito-Android cannot mock final classes in Android Instrumentation tests
 * and "opening" (i.e. add keyword "open") the class somehow doesn't solve the problem.
 */
interface HostCommunicator : GameCommunicator {

    fun sendMessage(envelopeToSend: EnvelopeToSend): Completable

    fun sendMessages(envelopeToSendList: List<EnvelopeToSend>): Completable

    fun listenForConnections()

    fun observeCommunicationState(): Observable<HostCommunicationState>

    fun closeAllConnections()

    fun kickPlayer(localConnectionId: LocalConnectionId)
}