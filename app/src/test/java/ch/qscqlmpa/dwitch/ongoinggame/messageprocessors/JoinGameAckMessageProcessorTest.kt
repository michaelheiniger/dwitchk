package ch.qscqlmpa.dwitch.components.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.components.game.TestEntityFactory
import ch.qscqlmpa.dwitch.components.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.components.ongoinggame.messages.Message
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinGameAckMessageProcessorTest : BaseMessageProcessorTest() {

    private val gameCommonId = 1L
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
        return processor.process(Message.JoinGameAckMessage(gameCommonId, guestPlayerInGameId), LocalConnectionId(0))
    }
}