package ch.qscqlmpa.dwitchgame.ingame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.JoinGameAckMessageProcessor
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.every
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class JoinGameAckMessageProcessorTest : BaseMessageProcessorTest() {

    private val gameCommonId = GameCommonId(1L)
    private val guestPlayerDwitchId = TestEntityFactory.createGuestPlayer1().dwitchId

    private lateinit var processor: JoinGameAckMessageProcessor

    @BeforeEach
    fun setup() {
        processor = JoinGameAckMessageProcessor(mockInGameStore)
    }

    @Test
    fun `Update game with common ID when join ack message is received`() {
        every { mockInGameStore.updateLocalPlayerWithDwitchId(any()) } returns 1

        launchTest().test().assertComplete()

        verify { mockInGameStore.updateGameWithCommonId(gameCommonId) }
    }

    @Test
    fun `Update local player with in-game ID when join ack message is received`() {
        every { mockInGameStore.updateLocalPlayerWithDwitchId(any()) } returns 1

        launchTest().test().assertComplete()

        verify { mockInGameStore.updateLocalPlayerWithDwitchId(guestPlayerDwitchId) }
    }

    private fun launchTest(): Completable {
        return processor.process(Message.JoinGameAckMessage(gameCommonId, guestPlayerDwitchId), ConnectionId(0))
    }
}
