package ch.qscqlmpa.dwitchgame.ongoinggame.communication

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Observable

internal interface GameCommunicator {

    fun observePlayerConnectionState(): Observable<PlayerConnectionState>

    fun sendMessageToHost(message: Message)
}