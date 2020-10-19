package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import com.jakewharton.rxrelay2.PublishRelay
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class GuestCommunicatorImplTest : BaseUnitTest() {

    private val mockCommClient = mockk<CommClient>()

    private val mockMessageDispatcher = mockk<MessageDispatcher>()

    private val mockCommunicationEventDispatcher = mockk<GuestCommunicationEventDispatcher>()

    private lateinit var guestCommunicator: GuestCommunicatorImpl

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(Schedulers.computation())

        guestCommunicator = GuestCommunicatorImpl(
                mockCommClient,
                mockMessageDispatcher,
                mockCommunicationEventDispatcher,
                schedulerFactory
        )

        every { mockCommClient.start() } just Runs
        every { mockCommClient.stop() } just Runs
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommClient, mockMessageDispatcher, mockCommunicationEventDispatcher)
    }

    @Test
    fun startObservingReceivedMessages() {

        setupEmptyCommunicationEventMock()
        setupEmptyReceivedMessageMock()

        guestCommunicator.connect()
        guestCommunicator.connect() // should be idempotent

        verify { mockCommClient.start() }
        verify { mockCommClient.observeReceivedMessages() }
        verify { mockCommClient.observeCommunicationEvents() }

        confirmVerified(mockCommClient)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    @Test
    fun dispatchReceivedMessage() {
        setupMessageDispatchCompleteMock()
        setupEmptyCommunicationEventMock()

        val messageReceived1 = mockk<EnvelopeReceived>()
        val messageReceived2 = mockk<EnvelopeReceived>()
        setupReceivedMessageMock(listOf(messageReceived1, messageReceived2))

        guestCommunicator.connect()

        val dispatchedMessageReceivedWrapperCap = mutableListOf<EnvelopeReceived>()
        verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageReceivedWrapperCap)) }

        assertEquals(messageReceived1, dispatchedMessageReceivedWrapperCap[0])
        assertEquals(messageReceived2, dispatchedMessageReceivedWrapperCap[1])

        verify { mockCommClient.start() }
        verify { mockCommClient.observeReceivedMessages() }
        verify { mockCommClient.observeCommunicationEvents() }

        confirmVerified(mockCommClient)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    @Test
    fun observeCommunicationState() {
        setupEmptyReceivedMessageMock()
        setupMessageDispatchCompleteMock()
        setupCommunicationEventDispatcherMock()

        val communicationEventsSource = PublishRelay.create<ClientCommunicationEvent>()
        every { mockCommClient.observeCommunicationEvents() } returns communicationEventsSource
        guestCommunicator.connect()

        val testObserver1 = guestCommunicator.observeCommunicationState().test()

        communicationEventsSource.accept(ConnectedToHost)
        testObserver1.assertValue(GuestCommunicationState.CONNECTED)
        testObserver1.dispose()

        val testObserver2 = guestCommunicator.observeCommunicationState().test()
        testObserver2.assertValue(GuestCommunicationState.CONNECTED)

        communicationEventsSource.accept(DisconnectedFromHost)
        testObserver2.assertValues(GuestCommunicationState.CONNECTED, GuestCommunicationState.DISCONNECTED)

        verify(exactly = 1) { mockCommunicationEventDispatcher.dispatch(ConnectedToHost) }
        verify(exactly = 1) { mockCommunicationEventDispatcher.dispatch(DisconnectedFromHost) }

        verify(exactly = 1) { mockCommClient.start() }
        verify(exactly = 1) { mockCommClient.observeReceivedMessages() }
        verify(exactly = 1) { mockCommClient.observeCommunicationEvents() }

        confirmVerified(mockCommClient)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    @Test
    fun sendMessage() {
        setupEmptyCommunicationEventMock()
        setupEmptyReceivedMessageMock()

        every { mockCommClient.sendMessage(any()) } returns Completable.complete()

        val playerReadyMessage = Message.PlayerReadyMessage(PlayerInGameId(2), true)
        val messageWrapper = EnvelopeToSend(RecipientType.All, playerReadyMessage)

        guestCommunicator.sendMessage(messageWrapper)

        val messageCap = CapturingSlot<Message>()
        verify { mockCommClient.sendMessage(capture(messageCap)) }

        val playerReadyMessageCap = messageCap.captured as Message.PlayerReadyMessage
        assertEquals(PlayerInGameId(2), playerReadyMessageCap.playerInGameId)
        assertTrue(playerReadyMessageCap.ready)

        confirmVerified(mockCommClient)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    private fun setupReceivedMessageMock(envelopes: List<EnvelopeReceived>) {
        every { mockCommClient.observeReceivedMessages() } returns Observable.fromIterable(envelopes)
    }

    private fun setupEmptyCommunicationEventMock() {
        every { mockCommClient.observeCommunicationEvents() } returns Observable.empty<ClientCommunicationEvent>()
    }

    private fun setupEmptyReceivedMessageMock() {
        every { mockCommClient.observeReceivedMessages() } returns Observable.empty<EnvelopeReceived>()
    }

    private fun setupMessageDispatchCompleteMock() {
        every { mockMessageDispatcher.dispatch(any()) } returns Completable.complete()
    }

    private fun setupCommunicationEventDispatcherMock() {
        every { mockCommunicationEventDispatcher.dispatch(any()) } returns Completable.complete()
    }
}