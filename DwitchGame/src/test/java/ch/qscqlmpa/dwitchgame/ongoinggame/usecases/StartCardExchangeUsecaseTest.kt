package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.TestDwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StartCardExchangeUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>()
    private val mockConnectionStore = mockk<ConnectionStore>()
    private val mockDwitchEngine = mockk<DwitchEngine>()
    private lateinit var dwitchEngineFactory: TestDwitchEngineFactory

    private lateinit var usecase: StartCardExchangeUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        dwitchEngineFactory = TestDwitchEngineFactory()
        usecase = StartCardExchangeUsecase(mockCommunicator, mockConnectionStore, dwitchEngineFactory)
        every { mockCommunicator.sendMessage(any())} returns Completable.complete()
    }

    @Test
    fun `Send card-exchange message to each player that has to exchange cards`() {
        val player1DwitchId = PlayerDwitchId(1)
        val player2DwitchId = PlayerDwitchId(2)
        val connectionId1 = ConnectionId(10)
        val connectionId2 = ConnectionId(11)
        val cardExchange1 = CardExchange(player1DwitchId, 2, listOf(CardName.Two, CardName.Ace))
        val cardExchange2 = CardExchange(player2DwitchId, 2, listOf(CardName.Three, CardName.Four))
        every { mockConnectionStore.getConnectionId(player1DwitchId) } returns connectionId1
        every { mockConnectionStore.getConnectionId(player2DwitchId) } returns connectionId2
        every { mockDwitchEngine.getCardsExchanges() } returns listOf(cardExchange1, cardExchange2)
        dwitchEngineFactory.setInstance(mockDwitchEngine)

        usecase.startCardExchange(mockk()).test().assertComplete()

        verify { mockCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(connectionId1), Message.CardExchangeMessage(cardExchange1))) }
        verify { mockCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(connectionId2), Message.CardExchangeMessage(cardExchange2))) }
    }
}