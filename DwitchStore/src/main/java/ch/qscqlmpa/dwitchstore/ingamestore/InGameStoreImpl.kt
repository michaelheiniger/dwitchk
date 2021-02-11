package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.dao.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import ch.qscqlmpa.dwitchstore.ingamestore.model.GameCommonIdAndCurrentRoom
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

    // Game
    override fun getGame(): Game {
        return gameDao.getGame(gameLocalId)
    }

    override fun getGameCommonId(): GameCommonId {
        return gameDao.getGameCommonId(gameLocalId)
    }

    override fun getCurrentRoom(): RoomType {
        return getGame().currentRoom
    }

    override fun getGameState(): GameState {
        val game = gameDao.getGame(gameLocalId)
        return serializerFactory.unserializeGameState(game.gameState!!)
    }

    override fun gameIsNew(): Boolean {
        return gameDao.getGame(gameLocalId).isNew()
    }

    override fun observeGameState(): Observable<GameState> {
        return gameDao.observeGame(gameLocalId)
            .map { game -> serializerFactory.unserializeGameState(game.gameState!!) }
    }

    override fun getGameCommonIdAndCurrentRoom(): GameCommonIdAndCurrentRoom {
        return gameDao.getGameCommonIdAndCurrentRoom(gameLocalId)
    }

    override fun getPlayerLocalId(dwitchId: PlayerDwitchId): Long? {
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

    override fun updateGameState(gameState: GameState) {
        val serializedGameState = serializerFactory.serialize(gameState)
        gameDao.updateGameState(gameLocalId, serializedGameState)
    }

    override fun addCardExchangeEvent(cardExchange: CardExchange) {
        gameDao.addCardExchangeEvent(gameLocalId, serializerFactory.serialize(cardExchange))
    }

    override fun getCardExchangeInfo(): CardExchangeInfo {
        val game = gameDao.getGame(gameLocalId)
        val localPlayer = playerDao.getPlayer(localPlayerLocalId)
        val cardsInHand = serializerFactory.unserializeGameState(game.gameState!!).player(localPlayer.dwitchId).cardsInHand
        val cardExchange = serializerFactory.unserializeCardExchange(game.cardExchangeEvent!!)
        return CardExchangeInfo(cardExchange, cardsInHand)
    }

    override fun observeCardExchangeEvents(): Observable<CardExchange> {
        return gameDao.observeGame(gameLocalId)
            .filter { game -> game.cardExchangeEvent != null }
            .map { game -> serializerFactory.unserializeCardExchange(game.cardExchangeEvent!!) }
    }

    override fun deleteCardExchangeEvent() {
        return gameDao.deleteCardExchangeEvent(gameLocalId)
    }

    // Player
    override fun insertNewGuestPlayer(name: String): Long {
        return playerDao.insertNewGuestPlayer(gameLocalId, name)
    }

    override fun insertPlayers(players: List<Player>): List<Long> {
        return playerDao.insertPlayers(players.map { p -> p.copy(gameLocalId = gameLocalId) })
    }

    override fun updateLocalPlayerWithDwitchId(playerDwitchId: PlayerDwitchId): Int {
        return playerDao.updatePlayerWithDwitchId(localPlayerLocalId, playerDwitchId)
    }

    override fun updateLocalPlayerWithReady(ready: Boolean): Int {
        return playerDao.updatePlayerWithReady(localPlayerLocalId, ready)
    }

    override fun updatePlayerWithReady(playerDwitchId: PlayerDwitchId, ready: Boolean): Int {
        return playerDao.updatePlayerWithReady(playerDwitchId, ready)
    }

    override fun updatePlayer(
        playerDwitchId: PlayerDwitchId,
        state: PlayerConnectionState,
        ready: Boolean
    ): Int {
        return playerDao.updatePlayer(playerDwitchId, state, ready)
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

    override fun deletePlayer(playerDwitchId: PlayerDwitchId): Int {
        return playerDao.deletePlayer(gameLocalId, playerDwitchId)
    }

    override fun getLocalPlayer(): Player {
        return playerDao.gePlayer(localPlayerLocalId)
    }

    override fun observeLocalPlayer(): Observable<Player> {
        return playerDao.observePlayer(localPlayerLocalId)
    }

    override fun getLocalPlayerDwitchId(): PlayerDwitchId {
        return playerDao.gePlayer(localPlayerLocalId).dwitchId
    }

    override fun getPlayerDwitchId(playerLocalId: Long): PlayerDwitchId {
        return playerDao.getPlayer(playerLocalId).dwitchId
    }

    override fun getPlayer(playerDwitchId: PlayerDwitchId): Player {
        return playerDao.getPlayer(gameLocalId, playerDwitchId)
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
        return playerDao.getPlayersInWaitingRoom(gameLocalId)
    }
}
