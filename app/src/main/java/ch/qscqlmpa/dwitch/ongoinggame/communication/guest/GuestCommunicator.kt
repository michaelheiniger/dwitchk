package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Completable
import io.reactivex.Observable

interface GuestCommunicator : GameCommunicator {

    fun sendMessage(envelopeToSend: EnvelopeToSend): Completable

    fun observeCommunicationState(): Observable<GuestCommunicationState>

    fun connect()

    fun closeConnection()
}