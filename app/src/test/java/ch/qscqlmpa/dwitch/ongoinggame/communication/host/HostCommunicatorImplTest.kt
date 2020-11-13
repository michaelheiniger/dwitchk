package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.Address
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.AddressType
import ch.qscqlmpa.dwitch.ongoinggame.events.HostCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HostCommunicatorImplTest : BaseUnitTest() {

    private val mockCommServer = mockk<CommServer>(relaxed = true)

    private val mockMessageDispatcher = mockk<MessageDispatcher>(relaxed = true)

    private val mockCommunicationEventDispatcher = mockk<HostCommunicationEventDispatcher>(relaxed = true)

    private val mockCommEventRepository = mockk<HostCommunicationEventRepository>(relaxed = true)

    private lateinit var localConnectionIdStore: LocalConnectionIdStore

    private lateinit var hostCommunicator: HostCommunicator

    private lateinit var communicationEventsStream: PublishSubject<ServerCommunicationEvent>

    private lateinit var receivedMessagesStream: PublishSubject<EnvelopeReceived>

    @BeforeEach
    override fun setup() {
        super.setup()

        localConnectionIdStore = LocalConnectionIdStore()

        hostCommunicator = HostCommunicatorImpl(
            mockCommServer,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            localConnectionIdStore,
            TestSchedulerFactory()
        )

        communicationEventsStream = PublishSubject.create()
        every { mockCommServer.observeCommunicationEvents() } returns communicationEventsStream

        receivedMessagesStream = PublishSubject.create()
        every { mockCommServer.observeReceivedMessages() } returns receivedMessagesStream
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Nested
    inner class ListenForConnections {

        @Test
        fun `Start listening for connections`() {
            assertThat(communicationEventsStream.hasObservers()).isFalse

            hostCommunicator.listenForConnections()
            assertThat(communicationEventsStream.hasObservers()).isTrue

            verifyOrder {
                mockCommServer.observeCommunicationEvents()
                mockCommServer.observeReceivedMessages()
                mockCommServer.start()
            }
        }

        @Test
        fun `Communication events emitted by websocket server are dispatched`() {
            hostCommunicator.listenForConnections()

            communicationEventsStream.onNext(ServerCommunicationEvent.ListeningForConnections)
            communicationEventsStream.onNext(ServerCommunicationEvent.NotListeningForConnections)

            val dispatchedEventCap = mutableListOf<ServerCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }

            assertThat(dispatchedEventCap[0]).isEqualTo(ServerCommunicationEvent.ListeningForConnections)
            assertThat(dispatchedEventCap[1]).isEqualTo(ServerCommunicationEvent.NotListeningForConnections)

            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Received messages emitted by websocket server are dispatched`() {
            hostCommunicator.listenForConnections()

            val messageReceived1 = mockk<EnvelopeReceived>()
            val messageReceived2 = mockk<EnvelopeReceived>()
            receivedMessagesStream.onNext(messageReceived1)
            receivedMessagesStream.onNext(messageReceived2)

            val dispatchedMessageCap = mutableListOf<EnvelopeReceived>()
            verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageCap)) }

            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)

            confirmVerified(mockMessageDispatcher)
        }
    }

    @Nested
    inner class CloseAllConnections {

        @Test
        fun `Close all connections`() {
            hostCommunicator.listenForConnections()

            hostCommunicator.closeAllConnections()
            assertThat(communicationEventsStream.hasObservers()).isFalse // Observed streams have been disposed.

            verifyOrder {
                mockCommServer.observeCommunicationEvents()
                mockCommServer.observeReceivedMessages()
                mockCommServer.start()
                mockCommServer.stop()
            }
            confirmVerified(mockCommServer)
        }
    }

    @Nested
    inner class SendMessage {

        @BeforeEach
        fun setup() {
            every { mockCommServer.sendMessage(any(), any()) } returns Completable.complete()
        }

        @Test
        fun `Broadcast message when RecipientType is All`() {
            val messageToSend = Message.CancelGameMessage

            hostCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, messageToSend)).test().assertComplete()

            verify { mockCommServer.sendMessage(Message.CancelGameMessage, AddressType.Broadcast) }
        }

        @Test
        fun `Send message to recipient specified by RecipientType Single`() {
            val messageToSend = Message.JoinGameAckMessage(GameCommonId(124), PlayerInGameId(45))
            val recipientAddress = Address("192.168.1.1", 54245)
            val recipientLocalConnectionId = localConnectionIdStore.addConnectionId(recipientAddress)

            hostCommunicator.sendMessage(EnvelopeToSend(RecipientType.Single(recipientLocalConnectionId), messageToSend))
                .test().assertComplete()

            verify { mockCommServer.sendMessage(messageToSend, AddressType.Unicast(recipientAddress)) }
        }
    }

    @Nested
    inner class CloseConnectionWithClient {

        @Test
        fun `Close connection with the specified client `() {
            val clientLocalConnectionId = LocalConnectionId(234)

            hostCommunicator.closeConnectionWithClient(clientLocalConnectionId)

            verify { mockCommServer.closeConnectionWithClient(clientLocalConnectionId) }
        }
    }

    @Nested
    inner class ObserveCommunicationState {

        @Test
        fun `Communication events emitted by the repository are simply forwarded`() {
            every { mockCommEventRepository.observeEvents() } returns Observable.just(HostCommunicationState.ListeningForGuests)

            hostCommunicator.observeCommunicationState().test().assertValue(HostCommunicationState.ListeningForGuests)
        }
    }
}