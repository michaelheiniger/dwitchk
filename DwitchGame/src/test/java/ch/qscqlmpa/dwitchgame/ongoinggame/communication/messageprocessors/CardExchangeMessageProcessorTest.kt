package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors.BaseMessageProcessorTest
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CardExchangeMessageProcessorTest : BaseMessageProcessorTest() {

    private lateinit var processor: CardExchangeMessageProcessor

    private val localPlayerInGameId = PlayerInGameId(1)

     @BeforeEach
     override fun setup() {
         super.setup()
         processor = CardExchangeMessageProcessor(mockInGameStore)
         every { mockInGameStore.getLocalPlayerInGameId() } returns localPlayerInGameId
     }

    @Test
    fun `Store received card exchange event when the in-game ID matches the ID of the local player`() {
        val cardExchange = CardExchange(localPlayerInGameId, 2, listOf(CardName.Ace, CardName.King))

        processor.process(Message.CardExchangeMessage(cardExchange), ConnectionId(4543)).test().assertComplete()

        verify { mockInGameStore.addCardExchangeEvent(cardExchange) }
    }

    @Test
    fun `Ignore received card exchange event when the in-game ID does not match the ID of the local player`() {
        val otherPlayerInGameId = PlayerInGameId(2)
        assertThat(localPlayerInGameId).isNotEqualTo(otherPlayerInGameId)
        val cardExchange = CardExchange(otherPlayerInGameId, 2, listOf(CardName.Ace, CardName.King))

        processor.process(Message.CardExchangeMessage(cardExchange), ConnectionId(4543)).test().assertComplete()

        verify(exactly = 0) { mockInGameStore.addCardExchangeEvent(cardExchange) }
    }
}