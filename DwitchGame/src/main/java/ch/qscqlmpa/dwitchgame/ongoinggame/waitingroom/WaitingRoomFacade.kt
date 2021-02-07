package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import io.reactivex.rxjava3.core.Observable

interface WaitingRoomFacade {

    fun observePlayers(): Observable<List<PlayerWr>>
}
