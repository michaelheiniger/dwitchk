package ch.qscqlmpa.dwitch.ongoinggame.communication

import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Completable
import io.reactivex.Observable

interface GameCommunicator {

    fun observePlayerConnectionState(): Observable<PlayerConnectionState>

    fun sendMessage(envelopeToSend: EnvelopeToSend): Completable
}