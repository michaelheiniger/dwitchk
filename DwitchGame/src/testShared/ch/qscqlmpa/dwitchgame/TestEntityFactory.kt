package ch.qscqlmpa.dwitchgame

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.random.RandomInitialGameSetup
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import org.joda.time.DateTime

object TestEntityFactory {

    fun createPlayerWrUi1(): PlayerWrUi {
        return PlayerWrUi(11L, "Sheev", connected = true, ready = true, kickable = true)
    }

    fun createPlayerWrUi2(): PlayerWrUi {
        return PlayerWrUi(12L, "Obi-Wan", connected = true, ready = true, kickable = false)
    }

    fun createHostPlayer(
        localId: Long = 10L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(100),
        connected: Boolean = true,
        ready: Boolean = true
    ): Player {
        return Player(
            id = localId,
            dwitchId = dwitchId,
            gameLocalId = 1L,
            name = "Aragorn",
            playerRole = PlayerRole.HOST,
            connected = connected,
            ready = ready
        )
    }

    fun createGuestPlayer1(
        localId: Long = 11L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(101),
        connected: Boolean = true,
        ready: Boolean = true,
        computerManaged: Boolean = false
    ): Player {
        return Player(
            id = localId,
            dwitchId = dwitchId,
            gameLocalId = 1L,
            name = "Boromir",
            playerRole = PlayerRole.GUEST,
            connected = connected,
            ready = ready,
            computerManaged = computerManaged
        )
    }

    fun createGuestPlayer2(
        localId: Long = 12L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(102),
        connected: Boolean = true,
        ready: Boolean = true,
        computerManaged: Boolean = false
    ): Player {
        return Player(
            id = localId,
            dwitchId = dwitchId,
            gameLocalId = 1L,
            name = "Celeborn",
            playerRole = PlayerRole.GUEST,
            connected = connected,
            ready = ready,
            computerManaged = computerManaged
        )
    }

    fun createGuestPlayer3(
        localId: Long = 13L,
        dwitchId: DwitchPlayerId = DwitchPlayerId(103),
        connected: Boolean = true,
        ready: Boolean = true,
        computerManaged: Boolean = false
    ): Player {
        return Player(
            id = localId,
            dwitchId = dwitchId,
            gameLocalId = 1L,
            name = "Denethor",
            playerRole = PlayerRole.GUEST,
            connected = connected,
            ready = ready,
            computerManaged = computerManaged
        )
    }

    fun createGameInWaitingRoom(localPlayerLocalId: Long = 10): Game {
        return Game(
            id = 1L,
            creationDate = DateTime.now(),
            currentRoom = RoomType.WAITING_ROOM,
            gameCommonId = GameCommonId(65),
            name = "Dwitch",
            gameState = "",
            localPlayerLocalId = localPlayerLocalId
        )
    }

    fun createGameState(): DwitchGameState {
        val hostPlayer = createHostPlayer()
        val players = listOf(hostPlayer, createGuestPlayer1())
        return DwitchEngine.createNewGame(
            players.map { p -> DwitchPlayerOnboardingInfo(p.dwitchId, p.name) },
            RandomInitialGameSetup(players.map { p -> p.dwitchId }.toSet())
        )
    }
}
