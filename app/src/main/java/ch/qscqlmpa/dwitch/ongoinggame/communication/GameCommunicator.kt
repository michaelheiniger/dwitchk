package ch.qscqlmpa.dwitch.ongoinggame.communication

import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Completable

interface GameCommunicator {

    fun sendGameState(envelopeToSend: EnvelopeToSend): Completable
}