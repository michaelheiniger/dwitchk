package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.jupiter.api.AfterEach

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
        every { mockGameCommunicator.sendGameState(any()) } returns Completable.complete()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockHostMessageFactory, mockHostCommunicator, mockGuestCommunicator)
    }

    protected fun setupWaitingRoomStateUpdateMessageMock(): EnvelopeToSend {
        val waitingRoomStateUpdateMessageWrapperMock = mockk<EnvelopeToSend>()
        every { mockHostMessageFactory.createWaitingRoomStateUpdateMessage() } returns Single.just(waitingRoomStateUpdateMessageWrapperMock)
        return waitingRoomStateUpdateMessageWrapperMock
    }
}