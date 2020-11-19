package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import io.reactivex.Observable

interface WaitingRoomFacade {

    fun observePlayers(): Observable<List<PlayerWr>>
}