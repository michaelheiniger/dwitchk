package ch.qscqlmpa.dwitch.ongoinggame.persistence

import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.GAME_LOCAL_ID
import ch.qscqlmpa.dwitch.components.ongoinggame.OnGoingGameQualifiers.LOCAL_PLAYER_LOCAL_ID
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.persistence.AppRoomDatabase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Named

class InGameStoreImpl @Inject constructor(@Named(GAME_LOCAL_ID) private val gameLocalId: Long,
                                          @Named(LOCAL_PLAYER_LOCAL_ID) private val localPlayerLocalId: Long,
                                          database: AppRoomDatabase,
                                          private val serializerFactory: SerializerFactory
) : InGameStore {

    private val gameDao = database.gameDao()
    private val playerDao = database.playerDao()

    // Game
    override fun getGame(): Game {
        return gameDao.getGame(gameLocalId)
    }

    override fun getGameState(): GameState {
        val game = gameDao.getGame(gameLocalId)
        return serializerFactory.unserializeGameState(game.gameState)
    }

    override fun observeGameState(): Observable<GameState> {
        return gameDao.observeGame(gameLocalId)
                .map { game -> serializerFactory.unserializeGameState(game.gameState) }
    }

    override fun deleteGame() {
        gameDao.deleteGame(gameLocalId)
    }

    override fun updateGameRoom(gameRoom: RoomType) {
        gameDao.updateGameRoom(gameLocalId, gameRoom)
    }

    override fun updateGameState(gameState: GameState) {
        val serializedGameState = serializerFactory.serialize(gameState)
        gameDao.updateGameState(gameLocalId, serializedGameState)
    }

    // Player
    override fun insertNewGuestPlayer(name: String): Long {
        return playerDao.insertNewGuestPlayer(gameLocalId, name)
    }

    override fun insertNonLocalPlayer(player: Player): Long {
        return playerDao.insertNonLocalPlayer(gameLocalId, player)
    }

    override fun updateLocalPlayerWithInGameId(playerInGameId: PlayerInGameId): Int {
        return playerDao.updatePlayerWithInGameId(localPlayerLocalId, playerInGameId)
    }

    override fun updateLocalPlayerWithReady(ready: Boolean): Int {
        return playerDao.updatePlayerWithReady(localPlayerLocalId, ready)
    }

    override fun updatePlayerWithReady(playerInGameId: PlayerInGameId, ready: Boolean): Int {
        return playerDao.updatePlayerWithReady(playerInGameId, ready)
    }

    override fun updatePlayerWithConnectionState(playerInGameId: PlayerInGameId, state: PlayerConnectionState): Int {
        return playerDao.updatePlayerWithConnectionState(playerInGameId, state)
    }

    override fun updatePlayerWithConnectionStateAndReady(playerLocalId: Long, state: PlayerConnectionState, ready: Boolean): Int {
        return playerDao.updatePlayerWithConnectionStateAndReady(playerLocalId, state, ready)
    }

    override fun deletePlayers(playersLocalId: List<Long>): Int {
        return playerDao.deletePlayers(playersLocalId)
    }

    override fun deletePlayer(playerInGameId: PlayerInGameId): Int {
        return playerDao.deletePlayer(gameLocalId, playerInGameId)
    }

    override fun getLocalPlayer(): Player {
        return playerDao.getLocalPlayer(gameLocalId)
    }

    override fun getLocalPlayerInGameId(): PlayerInGameId {
        return playerDao.getLocalPlayer(gameLocalId).inGameId
    }

    override fun getPlayerInGameId(playerLocalId: Long): PlayerInGameId {
        return playerDao.getPlayer(playerLocalId).inGameId
    }

    override fun getPlayer(playerInGameId: PlayerInGameId): Player? {
        return playerDao.getPlayer(gameLocalId, playerInGameId)
    }

    override fun getPlayer(playerLocalId: Long): Player {
        return playerDao.getPlayer(playerLocalId)
    }

    /**
     * Return the list of connected players sorted on the name ASC
     */
    override fun observeConnectedPlayers(): Flowable<List<Player>> {
        return playerDao.observeConnectedPlayers(gameLocalId)
    }

    override fun getPlayersInWaitingRoom(): List<Player> {
        return playerDao.getPlayersInWaitingRoom(gameLocalId)
    }
}