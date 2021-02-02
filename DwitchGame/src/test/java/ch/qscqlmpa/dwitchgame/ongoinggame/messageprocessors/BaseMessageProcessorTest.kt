package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import io.mockk.every
import io.mockk.mockk

internal abstract class BaseMessageProcessorTest : BaseUnitTest() {

    protected val mockMessageFactory = mockk<MessageFactory>(relaxed = true)
    protected val mockHostMessageFactory = mockk<HostMessageFactory>(relaxed = true)
    protected val mockHostCommunicator = mockk<HostCommunicator>(relaxed = true)

    protected fun setupWaitingRoomStateUpdateMessageMock(): EnvelopeToSend {
        val mockEnvelope = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns mockEnvelope
        return mockEnvelope
    }

    protected fun setupGameStateUpdateMessageMock(): EnvelopeToSend {
        val mockMessage = mockk<Message>()
        every { mockMessageFactory.createGameStateUpdatedMessage() } returns mockMessage
        return EnvelopeToSend(Recipient.All, mockMessage)
    }
}