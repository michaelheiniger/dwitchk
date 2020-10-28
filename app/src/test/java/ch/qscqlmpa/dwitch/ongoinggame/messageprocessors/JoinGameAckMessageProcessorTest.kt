package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.mockk.every
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinGameAckMessageProcessorTest : BaseMessageProcessorTest() {

    private val guestPlayerInGameId = TestEntityFactory.createGuestPlayer1().inGameId

    private lateinit var processor: JoinGameAckMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = JoinGameAckMessageProcessor(mockInGameStore)
    }

    @Test
    fun `Update local player with in-game ID when "join ack" message is received`() {
        every { mockInGameStore.updateLocalPlayerWithInGameId(any()) } returns 1

        launchTest().test().assertComplete()

        verify { mockInGameStore.updateLocalPlayerWithInGameId(guestPlayerInGameId) }
    }

    private fun launchTest(): Completable {
        return processor.process(Message.JoinGameAckMessage(guestPlayerInGameId), LocalConnectionId(0))
    }
}