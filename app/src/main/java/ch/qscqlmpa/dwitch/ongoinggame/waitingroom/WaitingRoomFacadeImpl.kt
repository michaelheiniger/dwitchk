package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import io.reactivex.Observable
import javax.inject.Inject

internal class WaitingRoomFacadeImpl @Inject constructor(
    private val waitingRoomPlayerRepository: WaitingRoomPlayerRepository
) : WaitingRoomFacade{

    override fun observePlayers(): Observable<List<PlayerWr>> {
        return waitingRoomPlayerRepository.observePlayers()
    }
}