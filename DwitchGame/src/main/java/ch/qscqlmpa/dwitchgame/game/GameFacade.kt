package ch.qscqlmpa.dwitchgame.game

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface GameFacade {

    /**
     * Host a new game that other players can join as guests.
     * @param gameName name of the game
     * @param playerName name of the local player (the host) in the game
     */
    fun hostGame(gameName: String, playerName: String): Completable

    /**
     * List of existing games that can be resumed.
     */
    fun resumableGames(): Observable<List<ResumableGameInfo>>

    /**
     * Resume an existing game (see [resumableGames]). The local player becomes the host of the game (regardless of its role when the game was initially created).
     * An existing game can and must be joined by all the players initially present. If a new player wants to join the game or a player initially present is missing, then a new game must be created.
     * @param gameId local ID of the game to resume
     */
    fun resumeGame(gameId: Long): Completable

    /**
     * Join a new game (see [hostGame]) hosted by another player. The local player joins as a guest.
     * @param advertisedGame info of the game to join
     * @param playerName name of the local player
     */
    fun joinGame(advertisedGame: GameAdvertisingInfo, playerName: String): Completable

    /**
     * Join an existing game (see [resumeGame]) hosted by another player. The local player joins as a guest.
     * @param advertisedGame info of the game to join
     */
    fun joinResumedGame(advertisedGame: GameAdvertisingInfo): Completable
}
