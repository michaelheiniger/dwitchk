package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.model.GameCommonIdAndCurrentRoom
import ch.qscqlmpa.dwitchstore.ingamestore.model.ResumeComputerPlayersInfo
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

interface InGameStore {

    // Game
    fun getGame(): Game
    fun getGameCommonId(): GameCommonId
    fun getCurrentRoom(): RoomType
    fun getGameState(): DwitchGameState
    fun gameIsNew(): Boolean
    fun gameIsNotNew(): Boolean
    fun observeGameState(): Observable<DwitchGameState>
    fun getGameCommonIdAndCurrentRoom(): GameCommonIdAndCurrentRoom
    fun getPlayerLocalId(dwitchId: DwitchPlayerId): Long?

    fun updateGameWithCommonId(gameCommonId: GameCommonId)
    fun deleteGame()
    fun updateGameRoom(gameRoom: RoomType)
    fun updateGameState(gameState: DwitchGameState)

    // Player
    fun insertNewGuestPlayer(name: String, computerManaged: Boolean): Long
    fun insertPlayers(players: List<Player>): List<Long>

    fun updateLocalPlayerWithDwitchId(dwitchPlayerId: DwitchPlayerId): Int
    fun updateLocalPlayerWithReady(ready: Boolean): Int
    fun updatePlayerWithReady(dwitchPlayerId: DwitchPlayerId, ready: Boolean): Int
    fun updatePlayer(dwitchPlayerId: DwitchPlayerId, state: PlayerConnectionState, ready: Boolean): Int
    fun updatePlayerWithConnectionStateAndReady(playerLocalId: Long, state: PlayerConnectionState, ready: Boolean): Int
    fun updatePlayerWithConnectionState(playerLocalId: Long, state: PlayerConnectionState): Int
    fun setAllPlayersToDisconnected(): Int

    fun deletePlayers(playersLocalId: List<Long>): Int
    fun deletePlayer(dwitchPlayerId: DwitchPlayerId): Int

    fun getLocalPlayer(): Player
    fun observeLocalPlayer(): Observable<Player>
    fun getLocalPlayerDwitchId(): DwitchPlayerId
    fun getPlayerDwitchId(playerLocalId: Long): DwitchPlayerId
    fun getPlayer(dwitchPlayerId: DwitchPlayerId): Player?
    fun getPlayer(playerLocalId: Long): Player
    fun getPlayer(name: String): Player?

    /**
     * Return the list of connected players sorted on the name ASC
     */
    fun observePlayersInWaitingRoom(): Flowable<List<Player>>
    fun getPlayersInWaitingRoom(): List<Player>
    fun getComputerPlayersToResume(): ResumeComputerPlayersInfo
}
