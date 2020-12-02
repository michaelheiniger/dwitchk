package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InGameStore {

    // Game
    fun getGame(): Game
    fun getGameState(): GameState

    fun observeGameState(): Observable<GameState>
    fun updateGameWithCommonId(gameCommonId: GameCommonId)
    fun deleteGame()
    fun updateGameRoom(gameRoom: RoomType)
    fun updateGameState(gameState: GameState)

    // Player
    fun insertNewGuestPlayer(name: String): Long
    fun insertNonLocalPlayer(player: Player): Long

    fun updateLocalPlayerWithInGameId(playerInGameId: PlayerInGameId): Int
    fun updateLocalPlayerWithReady(ready: Boolean): Int
    fun updatePlayerWithReady(playerInGameId: PlayerInGameId, ready: Boolean): Int
    fun updatePlayer(playerInGameId: PlayerInGameId, state: PlayerConnectionState, ready: Boolean): Int
    fun updatePlayerWithConnectionStateAndReady(playerLocalId: Long, state: PlayerConnectionState, ready: Boolean): Int
    fun setAllPlayersToDisconnected(): Int

    fun deletePlayers(playersLocalId: List<Long>): Int
    fun deletePlayer(playerInGameId: PlayerInGameId): Int

    fun getLocalPlayer(): Player
    fun observeLocalPlayer(): Observable<Player>
    fun getLocalPlayerInGameId(): PlayerInGameId
    fun getPlayerInGameId(playerLocalId: Long): PlayerInGameId
    fun getPlayer(playerInGameId: PlayerInGameId): Player?
    fun getPlayer(playerLocalId: Long): Player
    fun getPlayer(name: String): Player?

    /**
     * Return the list of connected players sorted on the name ASC
     */
    fun observePlayersInWaitingRoom(): Flowable<List<Player>>
    fun getPlayersInWaitingRoom(): List<Player>
}