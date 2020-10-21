package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WaitingRoomStateUpdateMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var processor: WaitingRoomStateUpdateMessageProcessor

    private val hostPlayer = TestEntityFactory.createHostPlayer()
    private val localGuestPlayer = TestEntityFactory.createGuestPlayer1()
    private val guest2Player = TestEntityFactory.createGuestPlayer2(ready = true)
    private val guest3Player = TestEntityFactory.createGuestPlayer3(connectionState = PlayerConnectionState.CONNECTED)

    @BeforeEach
    override fun setup() {
        super.setup()
        localConnectionIdStore = LocalConnectionIdStore()
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