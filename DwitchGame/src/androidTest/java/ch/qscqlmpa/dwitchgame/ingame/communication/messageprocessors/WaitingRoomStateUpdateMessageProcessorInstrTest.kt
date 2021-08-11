package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.base.BaseInstrumentedTest
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class WaitingRoomStateUpdateMessageProcessorInstrTest : BaseInstrumentedTest() {

    private lateinit var processor: WaitingRoomStateUpdateMessageProcessor

    private val hostName = "Host"
    private val guest2PlayerName = "Guest2"
    private val guest3PlayerName = "Guest3"

    @Before
    fun setupProcessor() {
        processor = WaitingRoomStateUpdateMessageProcessor(inGameStore)
        inGameStore.updateLocalPlayerWithDwitchId(DwitchPlayerId(2))
    }

    @Test
    fun addHostToPlayers() {
        val playersBefore = inGameStore.observePlayersInWaitingRoom().blockingFirst()
        assertThat(playersBefore.size).isEqualTo(1)
        assertThat(playersBefore[0].name).isEqualTo(localPlayerName)

        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false),
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        val playersAfter = inGameStore.observePlayersInWaitingRoom().blockingFirst()
        assertThat(playersAfter.size).isEqualTo(2)
        assertThat(playersAfter[0].name).isEqualTo(hostName)
        assertThat(playersAfter[1].name).isEqualTo(localPlayerName)
    }

    @Test
    fun addGuest2AndGuest3WhoJoinedTheGame() {
        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false),
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(3), guest2PlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(4), guest3PlayerName, PlayerRole.GUEST, connected = true, ready = true),
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        val playersAfter = inGameStore.observePlayersInWaitingRoom().blockingFirst()
        assertThat(playersAfter.size).isEqualTo(4)
        assertThat(playersAfter[0].name).isEqualTo(guest2PlayerName)
        assertThat(playersAfter[1].name).isEqualTo(guest3PlayerName)
        assertThat(playersAfter[2].name).isEqualTo(hostName)
        assertThat(playersAfter[3].name).isEqualTo(localPlayerName)
    }

    @Test
    fun updateGuest2WithReadyStateAndGuest3WithConnectionStateWhoDisconnected() {
        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(3), guest2PlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(4), guest3PlayerName, PlayerRole.GUEST, connected = true, ready = true),
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        val playersBefore = inGameStore.observePlayersInWaitingRoom().blockingFirst()
        assertThat(playersBefore.size).isEqualTo(4)

        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false)
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        val playersAfter = inGameStore.observePlayersInWaitingRoom().blockingFirst()
        assertThat(playersAfter.size).isEqualTo(2)
        assertThat(playersAfter[0].name).isEqualTo(hostName)
        assertThat(playersAfter[1].name).isEqualTo(localPlayerName)
    }

    @Test
    fun removeGuest2AndGuest3WhoLeftTheGame() {
        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(3), guest2PlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(4), guest3PlayerName, PlayerRole.GUEST, connected = true, ready = true),
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        processor.process(
            Message.WaitingRoomStateUpdateMessage(
                listOf(
                    PlayerWr(DwitchPlayerId(1), hostName, PlayerRole.HOST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(2), localPlayerName, PlayerRole.GUEST, connected = true, ready = false),
                    PlayerWr(DwitchPlayerId(3), guest2PlayerName, PlayerRole.GUEST, connected = true, ready = true),
                    PlayerWr(DwitchPlayerId(4), guest3PlayerName, PlayerRole.GUEST, connected = false, ready = true),
                )
            ),
            ConnectionId(0)
        ).test().assertComplete()

        val players = inGameStore.observePlayersInWaitingRoom().blockingFirst()
        assertThat(players.size).isEqualTo(4)
        assertThat(players[0].name).isEqualTo(guest2PlayerName)
        assertThat(players[0].ready).isTrue
        assertThat(players[1].name).isEqualTo(guest3PlayerName)
        assertThat(players[1].connected).isFalse
        assertThat(players[2].name).isEqualTo(hostName)
        assertThat(players[3].name).isEqualTo(localPlayerName)
    }
}
