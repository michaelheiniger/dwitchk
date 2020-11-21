package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.WaitingRoomStateUpdateMessageProcessor
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WaitingRoomStateUpdateMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var connectionStore: ConnectionStore

    private lateinit var processor: WaitingRoomStateUpdateMessageProcessor

    private val hostPlayer = TestEntityFactory.createHostPlayer()
    private val localGuestPlayer = TestEntityFactory.createGuestPlayer1()
    private val guest2Player = TestEntityFactory.createGuestPlayer2(ready = true)
    private val guest3Player = TestEntityFactory.createGuestPlayer3(connectionState = PlayerConnectionState.CONNECTED)

    @BeforeEach
    override fun setup() {
        super.setup()
        connectionStore = ConnectionStoreFactory.createConnectionStore()
        processor = WaitingRoomStateUpdateMessageProcessor(mockInGameStore)
    }

    @Test
    fun `Remove guest2 and guest3 who left the game`() {
        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(
                hostPlayer,
                localGuestPlayer,
                guest2Player,
                guest3Player
        )

        val upToDatePlayerList = listOf(hostPlayer.copy(id = 855), localGuestPlayer.copy(id = 856))

        launchTest(upToDatePlayerList).test().assertComplete()

        verify { mockInGameStore.deletePlayers(listOf(guest2Player.id, guest3Player.id)) }

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Update guest2 with ready state and guest3 with connection state who left the game`() {
        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(
                hostPlayer,
                localGuestPlayer,
                guest2Player,
                guest3Player
        )

        val upToDatePlayerList = listOf(
                hostPlayer.copy(id = 855),
                localGuestPlayer.copy(id = 856),
                guest2Player.copy(id = 857, ready = false),
                guest3Player.copy(id = 858, connectionState = PlayerConnectionState.DISCONNECTED)
        )

        launchTest(upToDatePlayerList).test().assertComplete()

        verify { mockInGameStore.updatePlayerWithConnectionStateAndReady(guest2Player.id, PlayerConnectionState.CONNECTED, false) }
        verify { mockInGameStore.updatePlayerWithConnectionStateAndReady(guest3Player.id, PlayerConnectionState.DISCONNECTED, true) }

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Add guest2 and guest3 who joined the game`() {
        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(hostPlayer, localGuestPlayer)

        val upToDatePlayerList = listOf(
                hostPlayer.copy(id = 855),
                localGuestPlayer.copy(id = 856),
                guest2Player.copy(id = 857),
                guest3Player.copy(id = 858)
        )

        launchTest(upToDatePlayerList).test().assertComplete()

        verify { mockInGameStore.insertNonLocalPlayer(guest2Player.copy(id = 857)) }
        verify { mockInGameStore.insertNonLocalPlayer(guest3Player.copy(id = 858)) }

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest(upToDatePlayerList: List<Player>): Completable {
        return processor.process(Message.WaitingRoomStateUpdateMessage(upToDatePlayerList), LocalConnectionId(0))
    }
}