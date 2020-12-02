package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.AddressType
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class HostCommunicatorImplTest : BaseUnitTest() {

    private val mockCommServer = mockk<CommServer>(relaxed = true)

    private val mockMessageDispatcher = mockk<MessageDispatcher>(relaxed = true)

    private val mockCommunicationEventDispatcher = mockk<HostCommunicationEventDispatcher>(relaxed = true)

    private val mockCommEventRepository = mockk<HostCommunicationStateRepository>(relaxed = true)

    private lateinit var connectionStore: ConnectionStore

    private lateinit var hostCommunicator: HostCommunicator

    private lateinit var communicationEventsStream: PublishSubject<ServerCommunicationEvent>

    private lateinit var receivedMessagesStream: PublishSubject<EnvelopeReceived>

    @BeforeEach
    override fun setup() {
        super.setup()

        connectionStore = ConnectionStoreFactory.createConnectionStore()

        hostCommunicator = HostCommunicatorImpl(
            mockCommServer,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            connectionStore,
            TestSchedulerFactory()
        )

        communicationEventsStream = PublishSubject.create()
        every { mockCommServer.observeCommunicationEvents() } returns communicationEventsStream

        receivedMessagesStream = PublishSubject.create()
        every { mockCommServer.observeReceivedMessages() } returns receivedMessagesStream
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
            val recipientLocalConnectionId = connectionStore.addConnectionId(recipientAddress)

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
            every { mockCommEventRepository.observeEvents() } returns Observable.just(HostCommunicationState.Open)

            hostCommunicator.observeCommunicationState().test().assertValue(HostCommunicationState.Open)
        }
    }

    @Nested
    inner class ObservePlayerConnectionState {

        @Test
        fun `Communication state open is mapped to connected`() {
            every { mockCommEventRepository.observeEvents() } returns Observable.just(HostCommunicationState.Open)

            hostCommunicator.observePlayerConnectionState().test().assertValue(PlayerConnectionState.CONNECTED)
        }

        @Test
        fun `Communication state closed is mapped to disconnected`() {
            every { mockCommEventRepository.observeEvents() } returns Observable.just(HostCommunicationState.Closed)

            hostCommunicator.observePlayerConnectionState().test().assertValue(PlayerConnectionState.DISCONNECTED)
        }

        @Test
        fun `Communication state error is mapped to disconnected`() {
            every { mockCommEventRepository.observeEvents() } returns Observable.just(HostCommunicationState.Error)

            hostCommunicator.observePlayerConnectionState().test().assertValue(PlayerConnectionState.DISCONNECTED)
        }
    }
}