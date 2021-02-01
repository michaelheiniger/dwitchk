package ch.qscqlmpa.dwitchstore.ingamestore

import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface InGameStore {

    // Game
    fun getGame(): Game
    fun getCurrentRoom(): RoomType
    fun getGameState(): GameState
    fun gameIsNew(): Boolean
    fun observeGameState(): Observable<GameState>

    fun updateGameWithCommonId(gameCommonId: GameCommonId)
    fun deleteGame()
    fun updateGameRoom(gameRoom: RoomType)
    fun updateGameState(gameState: GameState)

    fun addCardExchangeEvent(cardExchange: CardExchange)
    fun getCardExchangeInfo(): Single<CardExchangeInfo>
    fun observeCardExchangeEvents(): Observable<CardExchange>

    fun deleteCardExchangeEvent()

    // Player
    fun insertNewGuestPlayer(name: String): Long
    fun insertNonLocalPlayer(player: Player): Long

    fun updateLocalPlayerWithDwitchId(playerDwitchId: PlayerDwitchId): Int
    fun updateLocalPlayerWithReady(ready: Boolean): Int
    fun updatePlayerWithReady(playerDwitchId: PlayerDwitchId, ready: Boolean): Int
    fun updatePlayer(playerDwitchId: PlayerDwitchId, state: PlayerConnectionState, ready: Boolean): Int
    fun updatePlayerWithConnectionStateAndReady(playerLocalId: Long, state: PlayerConnectionState, ready: Boolean): Int
    fun updatePlayerWithConnectionState(playerLocalId: Long, state: PlayerConnectionState): Int
    fun setAllPlayersToDisconnected(): Int

    fun deletePlayers(playersLocalId: List<Long>): Int
    fun deletePlayer(playerDwitchId: PlayerDwitchId): Int

    fun getLocalPlayer(): Player
    fun observeLocalPlayer(): Observable<Player>
    fun getLocalPlayerDwitchId(): PlayerDwitchId
    fun getPlayerDwitchId(playerLocalId: Long): PlayerDwitchId
    fun getPlayer(playerDwitchId: PlayerDwitchId): Player?
    fun getPlayer(playerLocalId: Long): Player
    fun getPlayer(name: String): Player?

    /**
     * Return the list of connected players sorted on the name ASC
     */
    fun observePlayersInWaitingRoom(): Flowable<List<Player>>
    fun getPlayersInWaitingRoom(): List<Player>
}