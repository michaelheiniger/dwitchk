package ch.qscqlmpa.dwitchgame.ongoinggame.communication

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.Completable
import io.reactivex.Observable

interface GameCommunicator {

    fun observePlayerConnectionState(): Observable<PlayerConnectionState>

    fun sendMessage(envelopeToSend: EnvelopeToSend): Completable
}