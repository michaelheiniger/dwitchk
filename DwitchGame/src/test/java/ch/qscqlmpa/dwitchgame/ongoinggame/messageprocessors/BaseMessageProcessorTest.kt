package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

abstract class BaseMessageProcessorTest : BaseUnitTest() {

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
        val waitingRoomStateUpdateMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(waitingRoomStateUpdateMessageWrapperMock)
        return waitingRoomStateUpdateMessageWrapperMock
    }
}