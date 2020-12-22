package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreFactory
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
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

    private lateinit var communicationEventsSubject: PublishSubject<ServerCommunicationEvent>

    private lateinit var receivedMessagesSubject: PublishSubject<EnvelopeReceived>

    @BeforeEach
    override fun setup() {
        super.setup()

        connectionStore = ConnectionStoreFactory.createConnectionStore()

        hostCommunicator = HostCommunicatorImpl(
            mockInGameStore,
            mockCommServer,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            connectionStore,
            TestSchedulerFactory()
        )

        communicationEventsSubject = PublishSubject.create()
        every { mockCommServer.observeCommunicationEvents() } returns communicationEventsSubject

        receivedMessagesSubject = PublishSubject.create()
        every { mockCommServer.observeReceivedMessages() } returns receivedMessagesSubject
    }

    @Nested
    inner class ListenForConnections {

        @Test
        fun `Start listening for connections`() {
            assertThat(communicationEventsSubject.hasObservers()).isFalse

            hostCommunicator.listenForConnections()
            assertThat(communicationEventsSubject.hasObservers()).isTrue

            verifyOrder {
                mockCommServer.observeCommunicationEvents()
                mockCommServer.observeReceivedMessages()
                mockCommServer.start()
            }
        }

        @Test
        fun `Communication events emitted by server are dispatched`() {
            hostCommunicator.listenForConnections()

            communicationEventsSubject.onNext(ServerCommunicationEvent.ListeningForConnections(ConnectionId(0)))
            communicationEventsSubject.onNext(ServerCommunicationEvent.NotListeningForConnections)

            val dispatchedEventCap = mutableListOf<ServerCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }

            assertThat(dispatchedEventCap[0]).isEqualTo(ServerCommunicationEvent.ListeningForConnections(ConnectionId(0)))
            assertThat(dispatchedEventCap[1]).isEqualTo(ServerCommunicationEvent.NotListeningForConnections)

            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Received messages emitted by server are dispatched`() {
            hostCommunicator.listenForConnections()

            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.PlayerReadyMessage(PlayerInGameId(12), true))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(PlayerInGameId(13), false))
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            val dispatchedMessageCap = mutableListOf<EnvelopeReceived>()
            verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageCap)) }
            verify(exactly = 0) { mockCommServer.sendMessage(any(), any())}

            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)

            confirmVerified(mockMessageDispatcher)
        }

        @Test
        fun `Received game state update messages received are forwarded to all guests`() {
            hostCommunicator.listenForConnections()

            val gameState = TestEntityFactory.createGameState()
            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.GameStateUpdatedMessage(gameState))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(PlayerInGameId(13), false))
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            verify(exactly = 1) { mockCommServer.sendMessage(Message.GameStateUpdatedMessage(gameState), Recipient.AllGuests)}
        }
    }

    @Nested
    inner class CloseAllConnections {

        @Test
        fun `Close all connections`() {
            hostCommunicator.listenForConnections()

            hostCommunicator.closeAllConnections()
            assertThat(communicationEventsSubject.hasObservers()).isFalse // Observed streams have been disposed.

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
        fun `Broadcast message to all guests`() {
            val messageToSend = Message.CancelGameMessage

            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.AllGuests, messageToSend)).test().assertComplete()

            verify { mockCommServer.sendMessage(messageToSend, Recipient.AllGuests) }
        }

        @Test
        fun `Send message to one specific recipient`() {
            val messageToSend = Message.JoinGameAckMessage(GameCommonId(124), PlayerInGameId(45))
            val guestConnectionId = ConnectionId(32)

            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.SingleGuest(guestConnectionId), messageToSend))
                .test().assertComplete()

            verify { mockCommServer.sendMessage(messageToSend, Recipient.SingleGuest(guestConnectionId)) }
        }
    }

    @Nested
    inner class CloseConnectionWithClient {

        @Test
        fun `Close connection with the specified client `() {
            val clientConnectionId = ConnectionId(234)

            hostCommunicator.closeConnectionWithClient(clientConnectionId)

            verify { mockCommServer.closeConnectionWithClient(clientConnectionId) }
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