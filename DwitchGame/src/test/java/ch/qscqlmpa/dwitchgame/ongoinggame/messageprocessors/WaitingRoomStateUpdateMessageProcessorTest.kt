package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.EntityMapper
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.WaitingRoomStateUpdateMessageProcessor
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import ch.qscqlmpa.dwitchstore.model.Player
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WaitingRoomStateUpdateMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var processor: WaitingRoomStateUpdateMessageProcessor

    private val hostPlayer = TestEntityFactory.createHostPlayer()
    private val localGuestPlayer = TestEntityFactory.createGuestPlayer1()
    private val guest2Player = TestEntityFactory.createGuestPlayer2(ready = true)
    private val guest3Player = TestEntityFactory.createGuestPlayer3(connectionState = PlayerConnectionState.CONNECTED)

    @BeforeEach
    override fun setup() {
        super.setup()
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

        val upToDatePlayerList = listOf(EntityMapper.toPlayerWr(hostPlayer), EntityMapper.toPlayerWr(localGuestPlayer))

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
            EntityMapper.toPlayerWr(hostPlayer),
            EntityMapper.toPlayerWr(localGuestPlayer),
            EntityMapper.toPlayerWr(guest2Player).copy(ready = false),
            EntityMapper.toPlayerWr(guest3Player).copy(connectionState = PlayerConnectionState.DISCONNECTED)
        )

        launchTest(upToDatePlayerList).test().assertComplete()

        verify {
            mockInGameStore.updatePlayerWithConnectionStateAndReady(
                guest2Player.id,
                PlayerConnectionState.CONNECTED,
                false
            )
        }
        verify {
            mockInGameStore.updatePlayerWithConnectionStateAndReady(
                guest3Player.id,
                PlayerConnectionState.DISCONNECTED,
                true
            )
        }

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    @Test
    fun `Add guest2 and guest3 who joined the game`() {
        every { mockInGameStore.getPlayersInWaitingRoom() } returns listOf(hostPlayer, localGuestPlayer)

        val upToDatePlayerList = listOf(
            EntityMapper.toPlayerWr(hostPlayer),
            EntityMapper.toPlayerWr(localGuestPlayer),
            EntityMapper.toPlayerWr(guest2Player),
            EntityMapper.toPlayerWr(guest3Player)
        )

        launchTest(upToDatePlayerList).test().assertComplete()

        val argCap = CapturingSlot<List<Player>>()
        verify { mockInGameStore.insertPlayers(capture(argCap)) }

        assertThat(argCap.captured[0].id).isEqualTo(0)
        assertThat(argCap.captured[0].gameLocalId).isEqualTo(0)
        assertThat(argCap.captured[0].dwitchId).isEqualTo(guest2Player.dwitchId)
        assertThat(argCap.captured[0].name).isEqualTo(guest2Player.name)
        assertThat(argCap.captured[0].playerRole).isEqualTo(guest2Player.playerRole)
        assertThat(argCap.captured[0].connectionState).isEqualTo(guest2Player.connectionState)
        assertThat(argCap.captured[0].ready).isEqualTo(guest2Player.ready)

        assertThat(argCap.captured[1].id).isEqualTo(0)
        assertThat(argCap.captured[1].gameLocalId).isEqualTo(0)
        assertThat(argCap.captured[1].dwitchId).isEqualTo(guest3Player.dwitchId)
        assertThat(argCap.captured[1].name).isEqualTo(guest3Player.name)
        assertThat(argCap.captured[1].playerRole).isEqualTo(guest3Player.playerRole)
        assertThat(argCap.captured[1].connectionState).isEqualTo(guest3Player.connectionState)
        assertThat(argCap.captured[1].ready).isEqualTo(guest3Player.ready)

        verify { mockInGameStore.getPlayersInWaitingRoom() }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest(upToDatePlayerList: List<PlayerWr>): Completable {
        return processor.process(Message.WaitingRoomStateUpdateMessage(upToDatePlayerList), ConnectionId(0))
    }
}
