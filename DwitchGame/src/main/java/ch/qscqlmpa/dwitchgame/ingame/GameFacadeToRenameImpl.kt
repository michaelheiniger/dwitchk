package ch.qscqlmpa.dwitchgame.ingame

import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameFacadeToRenameImpl @Inject constructor(
    override val localPlayerRole: PlayerRole,
    private val store: InGameStore
) : GameFacadeToRename {
    override fun observeCurrentRoom(): Observable<RoomType> = store.observeCurrentRoom()
}
