package ch.qscqlmpa.dwitchgame

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import org.joda.time.DateTime

object TestEntityFactory {

    fun createPlayerWr1(): PlayerWr {
        return PlayerWr(PlayerDwitchId(1), "Sheev", PlayerRole.HOST, PlayerConnectionState.CONNECTED, true)
    }

    fun createPlayerWr2(): PlayerWr {
        return PlayerWr(PlayerDwitchId(2), "Obi-Wan", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true)
    }

    fun createHostPlayer(
        localId: Long = 10L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(100),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Aragorn",
            PlayerRole.HOST,
            connectionState,
            ready
        )
    }

    fun createGuestPlayer1(
        localId: Long = 11L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(101),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Boromir",
            PlayerRole.GUEST,
            connectionState,
            ready
        )
    }

    fun createGuestPlayer2(
        localId: Long = 12L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(102),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Celeborn",
            PlayerRole.GUEST,
            connectionState,
            ready
        )
    }

    fun createGuestPlayer3(
        localId: Long = 13L,
        dwitchId: PlayerDwitchId = PlayerDwitchId(103),
        connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
        ready: Boolean = true
    ): Player {
        return Player(
            localId,
            dwitchId,
            1L,
            "Denethor",
            PlayerRole.GUEST,
            connectionState,
            ready
        )
    }

    fun createGameInWaitingRoom(localPlayerLocalId: Long = 10): Game {
        return Game(
            1L,
            DateTime.now(),
            RoomType.WAITING_ROOM,
            GameCommonId(65),
            "Dwitch",
            "",
            localPlayerLocalId,
            null
        )
    }

    fun createGameState(): GameState {
        val hostPlayer = createHostPlayer()
        val players = listOf(hostPlayer, createGuestPlayer1())
        return DwitchEngine.createNewGame(
            players.map(Player::toPlayerInfo),
            RandomInitialGameSetup(players.size)
        )
    }
}
