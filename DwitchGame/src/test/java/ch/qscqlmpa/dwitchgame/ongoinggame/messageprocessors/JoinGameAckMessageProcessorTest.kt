package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.JoinGameAckMessageProcessor
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.every
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinGameAckMessageProcessorTest : BaseMessageProcessorTest() {

    private val gameCommonId = GameCommonId(1L)
    private val guestPlayerInGameId = TestEntityFactory.createGuestPlayer1().inGameId

    private lateinit var processor: JoinGameAckMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = JoinGameAckMessageProcessor(mockInGameStore)
    }

    @Test
    fun `Update game with common ID when "join ack" message is received`() {
        every { mockInGameStore.updateLocalPlayerWithInGameId(any()) } returns 1

        launchTest().test().assertComplete()

        verify { mockInGameStore.updateGameWithCommonId(gameCommonId) }
    }

    @Test
    fun `Update local player with in-game ID when "join ack" message is received`() {
        every { mockInGameStore.updateLocalPlayerWithInGameId(any()) } returns 1

        launchTest().test().assertComplete()

        verify { mockInGameStore.updateLocalPlayerWithInGameId(guestPlayerInGameId) }
    }

    private fun launchTest(): Completable {
        return processor.process(Message.JoinGameAckMessage(gameCommonId, guestPlayerInGameId), ConnectionId(0))
    }
}