package ch.qscqlmpa.dwitchgame.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single

abstract class BaseMessageProcessorTest : BaseUnitTest() {

    protected val mockHostMessageFactory = mockk<HostMessageFactory>(relaxed = true)
    protected val mockHostCommunicator = mockk<HostCommunicator>(relaxed = true)
    protected val mockGuestCommunicator = mockk<GuestCommunicator>(relaxed = true)
    protected val mockGameCommunicator = mockk<GameCommunicator>(relaxed = true)

    protected fun setupCommunicatorSendMessageCompleteMock() {
        every { mockHostCommunicator.sendMessage(any()) } returns Completable.complete()
        every { mockGuestCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    protected fun setupCommunicatorSendGameState() {
        every { mockGameCommunicator.sendMessage(any()) } returns Completable.complete()
    }

    protected fun setupWaitingRoomStateUpdateMessageMock(): EnvelopeToSend {
        val waitingRoomStateUpdateMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(waitingRoomStateUpdateMessageWrapperMock)
        return waitingRoomStateUpdateMessageWrapperMock
    }
}