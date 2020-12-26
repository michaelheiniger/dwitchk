package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.TestDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors.BaseMessageProcessorTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardsForExchangeMessageProcessorTest: BaseMessageProcessorTest() {

    private lateinit var dwitchEngineFactory: TestDwitchEngineFactory

    private lateinit var processor: CardsForExchangeMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        dwitchEngineFactory = TestDwitchEngineFactory()
        processor = CardsForExchangeMessageProcessor(mockInGameStore, dwitchEngineFactory, TestUtil.lazyOf(mockHostCommunicator))
        setupCommunicatorSendMessageCompleteMock()
    }

    @Test
    fun `Update game state with card exchange info from message`() {
        val mockEngine = mockk<DwitchEngine>();
        val mockUpdatedGameState  = mockk<GameState>(); // No point in testing the content since it comes from a mock
        every { mockEngine.chooseCardsForExchange(any(), any())} returns mockUpdatedGameState
        dwitchEngineFactory.setInstance(mockEngine)

        val message = Message.CardsForExchangeMessage(PlayerInGameId(324), setOf(Card.Clubs2, Card.Clubs3))
        processor.process(message, ConnectionId(45)).test().assertComplete()

        verify { mockInGameStore.updateGameState(mockUpdatedGameState) }
        verify { mockHostCommunicator.sendMessage(EnvelopeToSend(Recipient.All, MessageFactory.createGameStateUpdatedMessage(mockUpdatedGameState)))}
    }
}