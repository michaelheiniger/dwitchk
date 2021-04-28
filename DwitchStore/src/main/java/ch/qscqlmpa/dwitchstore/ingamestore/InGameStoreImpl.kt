package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.ingamestore.model.GameCommonIdAndCurrentRoom
import ch.qscqlmpa.dwitchstore.ingamestore.model.ResumeComputerPlayersInfo
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import ch.qscqlmpa.dwitchstore.util.SerializerFactory
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

internal class InGameStoreImpl constructor(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    database: AppRoomDatabase,
    private val serializerFactory: SerializerFactory
) : InGameStore {

    private val gameDao = database.gameDao()
    private val playerDao = database.playerDao()

    private val _gameCommonId: GameCommonId by lazy { gameDao.getGameCommonId(gameLocalId) }
    private val _gameName: String by lazy { gameDao.getGameName(gameLocalId) }
    private val _localDwitchPlayerId: DwitchPlayerId by lazy { playerDao.getPlayerDwitchId(localPlayerLocalId) }

    // Game
    override fun getGame(): Game {
        return gameDao.getGame(gameLocalId)
    }

    override fun getGameCommonId(): GameCommonId {
        return _gameCommonId
    }

    override fun getGameName(): String {
        return _gameName
    }

    override fun getCurrentRoom(): RoomType {
        return getGame().currentRoom
    }

    override fun getGameState(): DwitchGameState {
        val game = gameDao.getGame(gameLocalId)
        return serializerFactory.unserializeGameState(game.gameState!!)
    }

    override fun gameIsNew(): Boolean {
        return gameDao.getGame(gameLocalId).isNew()
    }

    override fun gameIsNotNew(): Boolean {
        return gameDao.getGame(gameLocalId).isNotNew()
    }

    override fun observeGameState(): Observable<DwitchGameState> {
        return gameDao.observeGame(gameLocalId)
            .map { game -> serializerFactory.unserializeGameState(game.gameState!!) }
    }

    override fun getGameCommonIdAndCurrentRoom(): GameCommonIdAndCurrentRoom {
        return gameDao.getGameCommonIdAndCurrentRoom(gameLocalId)
    }

    override fun getPlayerLocalId(dwitchId: DwitchPlayerId): Long? {
        return playerDao.getPlayerLocalId(gameLocalId, dwitchId)
    }

    override fun updateGameWithCommonId(gameCommonId: GameCommonId) {
        gameDao.updateGameWithCommonId(gameLocalId, gameCommonId)
    }

    override fun deleteGame() {
        gameDao.deleteGame(gameLocalId)
    }

    override fun updateGameRoom(gameRoom: RoomType) {
        gameDao.updateGameRoom(gameLocalId, gameRoom)
    }

    override fun updateGameState(gameState: DwitchGameState) {
        val serializedGameState = serializerFactory.serialize(gameState)
        gameDao.updateGameState(gameLocalId, serializedGameState)
    }

    // Player
    override fun insertNewGuestPlayer(name: String, computerManaged: Boolean): Long {
        return playerDao.insertNewGuestPlayer(gameLocalId, name, computerManaged)
    }

    override fun insertPlayers(players: List<Player>): List<Long> {
        return playerDao.insertPlayers(players.map { p -> p.copy(gameLocalId = gameLocalId) })
    }

    override fun updateLocalPlayerWithDwitchId(dwitchPlayerId: DwitchPlayerId): Int {
        return playerDao.updatePlayerWithDwitchId(localPlayerLocalId, dwitchPlayerId)
    }

    override fun updateLocalPlayerWithReady(ready: Boolean): Int {
        return playerDao.updatePlayerWithReady(localPlayerLocalId, ready)
    }

    override fun updatePlayerWithReady(dwitchPlayerId: DwitchPlayerId, ready: Boolean): Int {
        return playerDao.updatePlayerWithReady(dwitchPlayerId, ready)
    }

    override fun updatePlayer(
        dwitchPlayerId: DwitchPlayerId,
        state: PlayerConnectionState,
        ready: Boolean
    ): Int {
        return playerDao.updatePlayer(dwitchPlayerId, state, ready)
    }

    override fun updatePlayerWithConnectionStateAndReady(
        playerLocalId: Long,
        state: PlayerConnectionState,
        ready: Boolean
    ): Int {
        return playerDao.updatePlayerWithConnectionStateAndReady(playerLocalId, state, ready)
    }

    override fun updatePlayerWithConnectionState(playerLocalId: Long, state: PlayerConnectionState): Int {
        return playerDao.updatePlayerWithConnectionState(playerLocalId, state)
    }

    override fun setAllPlayersToDisconnected(): Int {
        return playerDao.setAllPlayersToDisconnected(gameLocalId)
    }

    override fun deletePlayers(playersLocalId: List<Long>): Int {
        return playerDao.deletePlayers(playersLocalId)
    }

    override fun deletePlayer(dwitchPlayerId: DwitchPlayerId): Int {
        return playerDao.deletePlayer(gameLocalId, dwitchPlayerId)
    }

    override fun getLocalPlayer(): Player {
        return playerDao.gePlayer(localPlayerLocalId)
    }

    override fun observeLocalPlayer(): Observable<Player> {
        return playerDao.observePlayer(localPlayerLocalId)
    }

    override fun getLocalPlayerDwitchId(): DwitchPlayerId {
        return _localDwitchPlayerId
    }

    override fun getPlayerDwitchId(playerLocalId: Long): DwitchPlayerId {
        return playerDao.getPlayer(playerLocalId).dwitchId
    }

    override fun getPlayer(dwitchPlayerId: DwitchPlayerId): Player {
        return playerDao.getPlayer(gameLocalId, dwitchPlayerId)
    }

    override fun getPlayer(playerLocalId: Long): Player {
        return playerDao.getPlayer(playerLocalId)
    }

    override fun getPlayer(name: String): Player? {
        return playerDao.getPlayer(name)
    }

    /**
     * Return the list of connected players sorted on the name ASC
     */
    override fun observePlayersInWaitingRoom(): Flowable<List<Player>> {
        return playerDao.observePlayersInWaitingRoom(gameLocalId)
    }

    override fun getPlayersInWaitingRoom(): List<Player> {
        return playerDao.getPlayers(gameLocalId)
    }

    override fun getComputerPlayersToResume(): ResumeComputerPlayersInfo {
        return ResumeComputerPlayersInfo(_gameCommonId, playerDao.getComputerPlayersDwitchId(gameLocalId))
    }
}
