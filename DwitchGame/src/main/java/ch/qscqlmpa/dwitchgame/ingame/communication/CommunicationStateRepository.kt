package ch.qscqlmpa.dwitchgame.ingame.communication

import io.reactivex.rxjava3.core.Observable

internal interface CommunicationStateRepository {
    fun connectedToHost(): Observable<Boolean>
}
