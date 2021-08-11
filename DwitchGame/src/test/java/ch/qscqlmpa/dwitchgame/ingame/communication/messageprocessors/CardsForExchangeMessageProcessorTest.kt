package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.TestDwitchFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardsForExchangeMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var dwitchEngineFactory: TestDwitchFactory

    private lateinit var processor: CardsForExchangeMessageProcessor

    private val mockEngine = mockk<DwitchEngine>(relaxed = true)
    private val mockCurrentGameState = mockk<DwitchGameState>(relaxed = true)

    @BeforeEach
    fun setup() {
        dwitchEngineFactory = TestDwitchFactory()
        processor = CardsForExchangeMessageProcessor(mockInGameStore, dwitchEngineFactory, TestUtil.lazyOf(mockHostCommunicator))
        every { mockInGameStore.getGameState() } returns mockCurrentGameState
    }

    @Test
    fun `Update game state with card exchange info from message and send updated game state`() {
        val mockUpdatedGameState = mockk<DwitchGameState>() // No point in testing the content since it comes from a mock
        every { mockEngine.chooseCardsForExchange(any(), any()) } returns mockUpdatedGameState
        every { mockEngine.getCardExchangeIfRequired(any()) } returns mockk()
        dwitchEngineFactory.setInstance(mockEngine)

        val message = Message.CardsForExchangeMessage(DwitchPlayerId(324), setOf(Card.Clubs2, Card.Clubs3))
        processor.process(message, ConnectionId(45)).test().assertComplete()

        verify { mockInGameStore.updateGameState(mockUpdatedGameState) }
        verify {
            mockHostCommunicator.sendMessage(
                EnvelopeToSend(
                    Recipient.All,
                    MessageFactory.createGameStateUpdatedMessage(mockUpdatedGameState)
                )
            )
        }
    }

    @Test
    fun `Ignore message because exchange is not required and send current game state`() {
        every { mockEngine.getCardExchangeIfRequired(any()) } returns null
        dwitchEngineFactory.setInstance(mockEngine)

        val message = Message.CardsForExchangeMessage(DwitchPlayerId(324), setOf(Card.Clubs2, Card.Clubs3))
        processor.process(message, ConnectionId(45)).test().assertComplete()

        verify(exactly = 0) { mockInGameStore.updateGameState(any()) }
        verify {
            mockHostCommunicator.sendMessage(
                EnvelopeToSend(
                    Recipient.All,
                    MessageFactory.createGameStateUpdatedMessage(mockCurrentGameState)
                )
            )
        }
    }
}
