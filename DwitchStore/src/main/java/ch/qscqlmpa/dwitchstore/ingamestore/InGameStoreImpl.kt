package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.DwitchEvent
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.db.AppRoomDatabase
import ch.qscqlmpa.dwitchstore.util.SerializerFactory
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

internal class InGameStoreImpl constructor(
    private val gameLocalId: Long,
    private val localPlayerLocalId: Long,
    database: AppRoomDatabase,
    private val serializerFactory: SerializerFactory
) : InGameStore {

    private val gameDao = database.gameDao()
    private val playerDao = database.playerDao()
    private val dwitchEventDao = database.dwitchEventDao()

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

    override fun insertDwitchEvent(event: DwitchEvent) {
        dwitchEventDao.insertEvent2(gameLocalId) { id -> serializerFactory.serialize(event.copyWithId(id)) }
    }

    override fun observeDwitchEvents(): Observable<DwitchEvent> {
        return dwitchEventDao.observeDwitchEvents(gameLocalId)
            .map { event -> serializerFactory.unserializeDwitchEvent(event.event) }
    }

    override fun getCardExchangeEvent(): Single<CardExchange> {
        return Single.fromCallable { gameDao.getGame(gameLocalId) }
            .map { game -> serializerFactory.unserializeCardExchange(game.cardExchangeEvent!!) }
    }

    override fun observeCardExchangeEvents(): Observable<CardExchange> {
        return gameDao.observeGame(gameLocalId)
            .filter { game -> game.cardExchangeEvent != null }
            .map { game -> serializerFactory.unserializeCardExchange(game.cardExchangeEvent!!) }
    }

    override fun deleteDwitchEvent(event: DwitchEvent): Int {
        return dwitchEventDao.deleteEvent(event.id)
    }

    override fun deleteDwitchEvent(eventId: Long): Int {
        TODO("Not yet implemented")
    }

    override fun deleteCardExchangeEvent() {
        return gameDao.deleteCardExchangeEvent(gameLocalId)
    }

    // Player
    override fun insertNewGuestPlayer(name: String): Long {
        return playerDao.insertNewGuestPlayer(gameLocalId, name)
    }

    override fun insertNonLocalPlayer(player: Player): Long {
        return playerDao.insertNonLocalPlayer(gameLocalId, player)
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