package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import io.reactivex.rxjava3.core.Observable

interface WaitingRoomFacade {

    fun observePlayers(): Observable<List<PlayerWrUi>>
}
