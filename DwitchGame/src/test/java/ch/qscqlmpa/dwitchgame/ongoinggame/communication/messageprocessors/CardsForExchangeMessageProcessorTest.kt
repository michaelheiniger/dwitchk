package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.TestUtil
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors.BaseMessageProcessorTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardsForExchangeMessageProcessorTest: BaseMessageProcessorTest() {

    private val mockGameRepository = mockk<GameRepository>(relaxed = true)

    private lateinit var processor: CardsForExchangeMessageProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processor = CardsForExchangeMessageProcessor(mockGameRepository, TestUtil.lazyOf(mockHostCommunicator))
        setupCommunicatorSendMessageCompleteMock()
        every { mockGameRepository.updateGameState(any()) } returns Completable.complete()
    }

    @Test
    fun `Update game state with card exchange info from message`() {
        val playerInGameId = PlayerInGameId(324)
        val cardsForExchange = setOf(Card.Clubs2, Card.Clubs3)
        val mockEngine = mockk<DwitchEngine>();
        val mockUpdatedGameState  = mockk<GameState>(); // No point in testing the content since it comes from a mock

        every { mockGameRepository.getGameEngineWithCurrentGameState() } returns Single.just(mockEngine)
        every { mockEngine.chooseCardsForExchange(playerInGameId, cardsForExchange)} returns mockUpdatedGameState

        processor.process(Message.CardsForExchangeMessage(playerInGameId, cardsForExchange), ConnectionId(45)).test().assertComplete()

        verify { mockGameRepository.updateGameState(mockUpdatedGameState) }
        verify { mockHostCommunicator.sendMessage(EnvelopeToSend(Recipient.All, MessageFactory.createGameStateUpdatedMessage(mockUpdatedGameState)))}
    }
}