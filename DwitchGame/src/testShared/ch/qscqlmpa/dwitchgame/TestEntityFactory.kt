package ch.qscqlmpa.dwitchgame

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWr
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole

object TestEntityFactory {

    fun createPlayerWr1(): PlayerWr {
        return PlayerWr(PlayerInGameId(1), "Sheev", true, PlayerConnectionState.CONNECTED)
    }

    fun createPlayerWr2(): PlayerWr {
        return PlayerWr(PlayerInGameId(2), "Obi-Wan", true, PlayerConnectionState.CONNECTED)
    }

    fun createHostPlayer(
            localId: Long = 10L,
            inGameId: PlayerInGameId = PlayerInGameId(100),
            connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
            ready: Boolean = true
    ): Player {
        return Player(
                localId,
                inGameId,
                1L,
                "Aragorn",
                PlayerRole.HOST,
                connectionState,
                ready
        )
    }

    fun createGuestPlayer1(
            localId: Long = 11L,
            inGameId: PlayerInGameId = PlayerInGameId(101),
            connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
            ready: Boolean = true
    ): Player {
        return Player(
                localId,
                inGameId,
                1L,
                "Boromir",
                PlayerRole.GUEST,
                connectionState,
                ready
        )
    }

    fun createGuestPlayer2(
            localId: Long = 12L,
            inGameId: PlayerInGameId = PlayerInGameId(102),
            connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
            ready: Boolean = true
    ): Player {
        return Player(
                localId,
                inGameId,
                1L,
                "Celeborn",
                PlayerRole.GUEST,
                connectionState,
                ready
        )
    }

    fun createGuestPlayer3(
            localId: Long = 13L,
            inGameId: PlayerInGameId = PlayerInGameId(103),
            connectionState: PlayerConnectionState = PlayerConnectionState.CONNECTED,
            ready: Boolean = true
    ): Player {
        return Player(
                localId,
                inGameId,
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
                RoomType.WAITING_ROOM,
                GameCommonId(65),
                "Dwitch",
                "",
                localPlayerLocalId
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