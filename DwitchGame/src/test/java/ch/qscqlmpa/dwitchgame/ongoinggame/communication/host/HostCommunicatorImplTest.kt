package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors.HostCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.*
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

    private val mockConnectionStore = mockk<ConnectionStore>(relaxed = true)

    private lateinit var hostCommunicator: HostCommunicator

    private lateinit var communicationEventsSubject: PublishSubject<ServerCommunicationEvent>

    private lateinit var receivedMessagesSubject: PublishSubject<EnvelopeReceived>

    private val hostPlayerDwitchId = DwitchPlayerId(123)
    private val hostConnectionId = ConnectionId(321)

    @BeforeEach
    override fun setup() {
        super.setup()

        hostCommunicator = HostCommunicatorImpl(
            mockInGameStore,
            mockCommServer,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            mockConnectionStore,
            TestSchedulerFactory(),
            mockk(relaxed = true)
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

            hostCommunicator.startServer()
            assertThat(communicationEventsSubject.hasObservers()).isTrue

            verifyOrder {
                mockCommServer.observeCommunicationEvents()
                mockCommServer.observeReceivedMessages()
                mockCommServer.start()
            }
        }

        @Test
        fun `Communication events emitted by server are dispatched`() {
            hostCommunicator.startServer()

            communicationEventsSubject.onNext(ServerCommunicationEvent.ListeningForConnections(ConnectionId(0)))
            communicationEventsSubject.onNext(ServerCommunicationEvent.NoLongerListeningForConnections)

            val dispatchedEventCap = mutableListOf<ServerCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }

            assertThat(dispatchedEventCap[0]).isEqualTo(ServerCommunicationEvent.ListeningForConnections(ConnectionId(0)))
            assertThat(dispatchedEventCap[1]).isEqualTo(ServerCommunicationEvent.NoLongerListeningForConnections)

            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Received messages emitted by server are dispatched`() {
            hostCommunicator.startServer()

            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.PlayerReadyMessage(DwitchPlayerId(12), true))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(DwitchPlayerId(13), false))
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            val dispatchedMessageCap = mutableListOf<EnvelopeReceived>()
            verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageCap)) }
            verify(exactly = 0) { mockCommServer.sendMessage(any(), any()) }

            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)

            confirmVerified(mockMessageDispatcher)
        }

        @Test
        fun `Received game state update messages received are forwarded to all guests`() {
            hostCommunicator.startServer()

            val gameState = TestEntityFactory.createGameState()
            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.GameStateUpdatedMessage(gameState))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(DwitchPlayerId(13), false))
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            verify(exactly = 1) { mockCommServer.sendMessage(Message.GameStateUpdatedMessage(gameState), Recipient.All) }
        }
    }

    @Nested
    inner class CloseAllConnections {

        @Test
        fun `Close all connections`() {
            hostCommunicator.startServer()

            hostCommunicator.stopServer()
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
        }

        @Test
        fun `Send message to a single recipient that happens to be the host`() {
            hostCommunicator.startServer()

            every { mockInGameStore.getLocalPlayerDwitchId() } returns hostPlayerDwitchId
            every { mockConnectionStore.getConnectionId(hostPlayerDwitchId) } returns hostConnectionId

            val messageToSend = Message.CardsForExchangeMessage(hostPlayerDwitchId, setOf(Card.Clubs2, Card.Clubs3))

            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(hostConnectionId), messageToSend))

            verify(exactly = 0) { mockCommServer.sendMessage(messageToSend, any()) }
            verify { mockMessageDispatcher.dispatch(EnvelopeReceived(hostConnectionId, messageToSend)) }
        }

        @Test
        fun `Broadcast message to all guests`() {
            val messageToSend = Message.CancelGameMessage

            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.All, messageToSend))

            verify { mockCommServer.sendMessage(messageToSend, Recipient.All) }
        }

        @Test
        fun `Send message to one specific guest`() {
            every { mockInGameStore.getLocalPlayerDwitchId() } returns hostPlayerDwitchId
            every { mockConnectionStore.getConnectionId(hostPlayerDwitchId) } returns hostConnectionId

            val messageToSend = Message.JoinGameAckMessage(GameCommonId(124), DwitchPlayerId(45))
            val guestConnectionId = ConnectionId(32)

            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(guestConnectionId), messageToSend))

            verify { mockCommServer.sendMessage(messageToSend, Recipient.Single(guestConnectionId)) }
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
}
