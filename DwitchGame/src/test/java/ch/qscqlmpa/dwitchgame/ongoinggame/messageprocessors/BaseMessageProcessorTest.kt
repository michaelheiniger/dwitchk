package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

abstract class BaseMessageProcessorTest : BaseUnitTest() {

    protected val mockMessageFactory = mockk<MessageFactory>(relaxed = true)
    protected val mockHostMessageFactory = mockk<HostMessageFactory>(relaxed = true)
    protected val mockHostCommunicator = mockk<HostCommunicator>(relaxed = true)
    protected val mockGuestCommunicator = mockk<GuestCommunicator>(relaxed = true)
    protected val mockGameCommunicator = mockk<GameCommunicator>(relaxed = true)

    protected fun setupCommunicatorSendMessageCompleteMock() {
        every { mockHostCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    protected fun setupCommunicatorSendMessageToHostCompleteMock() {
        every { mockGuestCommunicator.sendMessageToHost(any()) } returns Completable.complete()
    }

    protected fun setupCommunicatorSendGameState() {
        every { mockGameCommunicator.sendMessageToHost(any()) } returns Completable.complete()
    }

    protected fun setupWaitingRoomStateUpdateMessageMock(): EnvelopeToSend {
        val mockEnvelope = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(mockEnvelope)
        return mockEnvelope
    }

    protected fun setupGameStateUpdateMessageMock(): EnvelopeToSend {
        val mockMessage = mockk<Message>()
        every { mockMessageFactory.createGameStateUpdatedMessage() } returns Single.just(mockMessage)
        return EnvelopeToSend(Recipient.All, mockMessage)
    }
}