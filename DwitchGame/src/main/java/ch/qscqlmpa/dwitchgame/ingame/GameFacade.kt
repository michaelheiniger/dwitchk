package ch.qscqlmpa.dwitchgame.ingame

import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.reactivex.rxjava3.core.Observable

interface GameFacade {
    val localPlayerRole: PlayerRole
    fun observeCurrentRoom(): Observable<RoomType>
}