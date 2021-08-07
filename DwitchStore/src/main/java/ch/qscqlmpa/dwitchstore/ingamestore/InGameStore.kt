package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.model.GameCommonIdAndCurrentRoom
import ch.qscqlmpa.dwitchstore.ingamestore.model.ResumeComputerPlayersInfo
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Observable

interface InGameStore {

    // Game
    fun getGame(): Game
    fun getGameCommonId(): GameCommonId
    fun getGameName(): String
    fun getCurrentRoom(): RoomType
    fun observeCurrentRoom(): Observable<RoomType>
    fun getGameState(): DwitchGameState
    fun gameIsNew(): Boolean
    fun gameIsNotNew(): Boolean
    fun observeGameState(): Observable<DwitchGameState>
    fun getGameCommonIdAndCurrentRoom(): GameCommonIdAndCurrentRoom
    fun getPlayerLocalId(dwitchId: DwitchPlayerId): Long?
    fun getLocalPlayerRole(): PlayerRole

    fun updateGameWithCommonId(gameCommonId: GameCommonId)
    fun markGameForDeletion()
    fun updateCurrentRoom(room: RoomType)
    fun updateGameState(gameState: DwitchGameState)

    // Player
    fun insertNewGuestPlayer(name: String, computerManaged: Boolean): Long
    fun insertPlayers(players: List<Player>): List<Long>

    fun updateLocalPlayerWithDwitchId(dwitchPlayerId: DwitchPlayerId): Int
    fun updateLocalPlayerWithReady(ready: Boolean): Int
    fun updatePlayerWithReady(dwitchPlayerId: DwitchPlayerId, ready: Boolean): Int
    fun updatePlayer(dwitchPlayerId: DwitchPlayerId, connected: Boolean, ready: Boolean): Int
    fun updatePlayerWithConnectionStateAndReady(playerLocalId: Long, connected: Boolean, ready: Boolean): Int
    fun updatePlayerWithConnectionState(playerLocalId: Long, connected: Boolean): Int
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
    fun observePlayersInWaitingRoom(): Observable<List<Player>>
    fun getPlayersInWaitingRoom(): List<Player>
    fun getComputerPlayersToResume(): ResumeComputerPlayersInfo
}
