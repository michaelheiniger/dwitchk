package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.Address
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.AddressType
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
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HostCommunicatorImplTest : BaseUnitTest() {

    private val mockCommServer = mockk<CommServer>(relaxed = true)

    private val mockMessageDispatcher = mockk<MessageDispatcher>(relaxed = true)

    private val mockCommunicationEventDispatcher = mockk<HostCommunicationEventDispatcher>(relaxed = true)

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var hostCommunicator: HostCommunicatorImpl

    @BeforeEach
    override fun setup() {
        super.setup()

        localConnectionIdStore = LocalConnectionIdStore()

        val scheduler = TestSchedulerFactory()
        scheduler.setTimeScheduler(Schedulers.computation())

        hostCommunicator = HostCommunicatorImpl(
                mockCommServer,
                mockMessageDispatcher,
                mockCommunicationEventDispatcher,
                scheduler,
                localConnectionIdStore
        )

        every { mockCommServer.start() } just Runs
        every { mockCommServer.stop() } just Runs
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockCommServer, mockMessageDispatcher, mockCommunicationEventDispatcher)
    }

    @Test
    fun startObservingReceivedMessages() {
        setupEmptyCommunicationEventMock()
        setupEmptyReceivedMessageMock()

        hostCommunicator.listenForConnections()
        hostCommunicator.listenForConnections() // should be idempotent

        verify(exactly = 1) { mockCommServer.start() }
        verify(exactly = 1) { mockCommServer.observeReceivedMessages() }
        verify(exactly = 1) { mockCommServer.observeCommunicationEvents() }

        confirmVerified(mockCommServer)
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

        hostCommunicator.listenForConnections()

        val dispatchedMessageReceivedWrapperCap = mutableListOf<EnvelopeReceived>()
        verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageReceivedWrapperCap)) }

        Assert.assertEquals(messageReceived1, dispatchedMessageReceivedWrapperCap[0])
        Assert.assertEquals(messageReceived2, dispatchedMessageReceivedWrapperCap[1])

        verify(exactly = 1) { mockCommServer.start() }
        verify(exactly = 1) { mockCommServer.observeReceivedMessages() }
        verify(exactly = 1) { mockCommServer.observeCommunicationEvents() }

        confirmVerified(mockCommServer)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    @Test
    fun observeCommunicationState() {
        setupEmptyReceivedMessageMock()
        setupMessageDispatchCompleteMock()
        setupCommunicationEventDispatcherMock()

        val communicationEventsSource = PublishRelay.create<ServerCommunicationEvent>()
        every { mockCommServer.observeCommunicationEvents() } returns communicationEventsSource
        hostCommunicator.listenForConnections()

        val testObserver1 = hostCommunicator.observeCommunicationState().test()

        communicationEventsSource.accept(ListeningForConnections)
        testObserver1.assertValue(HostCommunicationState.LISTENING_FOR_GUESTS)
        testObserver1.dispose()

        val testObserver2 = hostCommunicator.observeCommunicationState().test()
        testObserver2.assertValue(HostCommunicationState.LISTENING_FOR_GUESTS)

        communicationEventsSource.accept(NotListeningForConnections)
        testObserver2.assertValues(HostCommunicationState.LISTENING_FOR_GUESTS, HostCommunicationState.NOT_LISTENING_FOR_GUESTS)

        verify(exactly = 1) { mockCommunicationEventDispatcher.dispatch(ListeningForConnections) }
        verify(exactly = 1) { mockCommunicationEventDispatcher.dispatch(NotListeningForConnections) }

        verify(exactly = 1) { mockCommServer.start() }
        verify(exactly = 1) { mockCommServer.observeReceivedMessages() }
        verify(exactly = 1) { mockCommServer.observeCommunicationEvents() }

        confirmVerified(mockCommServer)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    @Test
    fun sendMessage() {
        setupEmptyCommunicationEventMock()
        setupEmptyReceivedMessageMock()

        every { mockCommServer.sendMessage(any(), any()) } returns Completable.complete()

        val joinGameAckMessage = Message.JoinGameAckMessage(GameCommonId(1), PlayerInGameId(2))
        val messageWrapper = EnvelopeToSend(RecipientType.Single(LocalConnectionId(0)), joinGameAckMessage)

        val address = Address("192.168.1.1", 8889)
        localConnectionIdStore.addAddress(address)

        hostCommunicator.sendMessage(messageWrapper)

        val messageCap = CapturingSlot<Message>()
        verify(exactly = 1) { mockCommServer.sendMessage(capture(messageCap), AddressType.Unicast(address)) }

        val joinGameAckMessageCap = messageCap.captured as Message.JoinGameAckMessage

        Assert.assertEquals(GameCommonId(1), joinGameAckMessageCap.gameCommonId)
        Assert.assertEquals(PlayerInGameId(2), joinGameAckMessageCap.playerInGameId)

        confirmVerified(mockCommServer)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    @Test
    fun kickPlayer() {
        setupEmptyCommunicationEventMock()
        setupEmptyReceivedMessageMock()

        val localConnectionId = LocalConnectionId(0)
        every { mockCommServer.closeConnectionWithClient(localConnectionId) } just Runs

        hostCommunicator.closeConnectionWithClient(localConnectionId)

        verify(exactly = 1) { mockCommServer.closeConnectionWithClient(localConnectionId) }

        confirmVerified(mockCommServer)
        confirmVerified(mockMessageDispatcher)
        confirmVerified(mockCommunicationEventDispatcher)
    }

    private fun setupReceivedMessageMock(envelopes: List<EnvelopeReceived>) {
        every { mockCommServer.observeReceivedMessages() } returns Observable.fromIterable(envelopes)
    }

    private fun setupEmptyCommunicationEventMock() {
        every { mockCommServer.observeCommunicationEvents() } returns Observable.empty<ServerCommunicationEvent>()
    }

    private fun setupEmptyReceivedMessageMock() {
        every { mockCommServer.observeReceivedMessages() } returns Observable.empty<EnvelopeReceived>()
    }

    private fun setupMessageDispatchCompleteMock() {
        every { mockMessageDispatcher.dispatch(any()) } returns Completable.complete()
    }

    private fun setupCommunicationEventDispatcherMock() {
        every { mockCommunicationEventDispatcher.dispatch(any()) } returns Completable.complete()
    }
}