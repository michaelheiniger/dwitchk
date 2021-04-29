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
import io.reactivex.rxjava3.core.Completable
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
    private lateinit var computerCommunicator: ComputerCommunicator

    private lateinit var communicationEventsSubject: PublishSubject<ServerCommunicationEvent>
    private lateinit var receivedMessagesSubject: PublishSubject<EnvelopeReceived>

    @BeforeEach
    fun setup() {
        hostCommunicator = HostCommunicatorImpl(
            mockCommServer,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            TestSchedulerFactory(),
            mockk(relaxed = true)
        )
        computerCommunicator = hostCommunicator as HostCommunicatorImpl

        communicationEventsSubject = PublishSubject.create()
        every { mockCommServer.observeCommunicationEvents() } returns communicationEventsSubject

        receivedMessagesSubject = PublishSubject.create()
        every { mockCommServer.observeReceivedMessages() } returns receivedMessagesSubject

        every { mockCommunicationEventDispatcher.dispatch(any()) } returns Completable.complete()
    }

    @Nested
    inner class StartServer {

        @Test
        fun `Start communication server operations`() {
            // Given
            assertThat(communicationEventsSubject.hasObservers()).isFalse
            assertThat(receivedMessagesSubject.hasObservers()).isFalse

            // When
            hostCommunicator.startServer()

            // Then
            assertThat(communicationEventsSubject.hasObservers()).isTrue // Observe communication events
            assertThat(receivedMessagesSubject.hasObservers()).isTrue // Observe received messages

            verifyOrder {
                mockCommServer.observeCommunicationEvents()
                mockCommServer.observeReceivedMessages()
                mockCommServer.start()
            }
            confirmVerified(mockCommServer)
        }

        @Test
        fun `Communication events emitted by communication server are dispatched`() {
            // Given
            hostCommunicator.startServer()

            // When
            communicationEventsSubject.onNext(ServerCommunicationEvent.ListeningForConnections)
            communicationEventsSubject.onNext(ServerCommunicationEvent.NoLongerListeningForConnections)

            // Then
            val dispatchedEventCap = mutableListOf<ServerCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }
            assertThat(dispatchedEventCap[0]).isEqualTo(ServerCommunicationEvent.ListeningForConnections)
            assertThat(dispatchedEventCap[1]).isEqualTo(ServerCommunicationEvent.NoLongerListeningForConnections)
            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Messages received through communication server are dispatched`() {
            // Given
            hostCommunicator.startServer()

            // When
            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.PlayerReadyMessage(DwitchPlayerId(12), true))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(DwitchPlayerId(13), false))
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            // Then
            val dispatchedMessageCap = mutableListOf<EnvelopeReceived>()
            verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageCap)) }
            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)
            confirmVerified(mockMessageDispatcher)
        }

        @Test
        fun `Received game state update messages received are forwarded to all guests`() {
            // Given
            hostCommunicator.startServer()

            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()

            // When
            val gameState = TestEntityFactory.createGameState()
            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.GameStateUpdatedMessage(gameState))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(DwitchPlayerId(13), false))
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            // Then only GameStateUpdated message is forwarded, PlayerReadyMessage is not
            verify(exactly = 1) { mockCommServer.sendMessage(Message.GameStateUpdatedMessage(gameState), Recipient.All) }
            messagesToComputerPlayers.assertValue(EnvelopeToSend(Recipient.All, Message.GameStateUpdatedMessage(gameState)))
        }
    }

    @Nested
    inner class StopServer {

        @Test
        fun `Server communication is stopped and messages and communication events are no longer processed`() {
            // Given
            hostCommunicator.startServer()
            assertThat(communicationEventsSubject.hasObservers()).isTrue
            assertThat(receivedMessagesSubject.hasObservers()).isTrue

            // When
            hostCommunicator.stopServer()
            communicationEventsSubject.onNext(ServerCommunicationEvent.NoLongerListeningForConnections)

            // Then
            assertThat(communicationEventsSubject.hasObservers()).isFalse // Observed streams have been disposed
            assertThat(receivedMessagesSubject.hasObservers()).isFalse // Observed streams have been disposed

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
    inner class CloseConnectionWithClient {

        @Test
        fun `Close connection with the specified client `() {
            // Given
            val clientConnectionId = ConnectionId(234)

            // When
            hostCommunicator.closeConnectionWithClient(clientConnectionId)

            // Then
            verify { mockCommServer.closeConnectionWithClient(clientConnectionId) }
        }
    }

    @Nested
    inner class SendMessage {

        private val hostPlayerDwitchId = DwitchPlayerId(123)
        private val hostConnectionId = ConnectionStore.hostConnectionId
        private val humanGuestConnectionId = ConnectionId(11) // Must be outside of computer reserved range
        private val computerGuestConnectionId = ConnectionId(10)

        @BeforeEach
        fun setup() {
            every { mockInGameStore.getLocalPlayerDwitchId() } returns hostPlayerDwitchId
            every { mockConnectionStore.getConnectionId(hostPlayerDwitchId) } returns hostConnectionId
        }

        @Test
        fun `Send message to a specific human guest`() {
            // Given
            val messageToSend = Message.JoinGameAckMessage(GameCommonId(124), DwitchPlayerId(45))

            // When
            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()
            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(humanGuestConnectionId), messageToSend))

            // Then
            verify(exactly = 0) { mockMessageDispatcher.dispatch(EnvelopeReceived(hostConnectionId, messageToSend)) }
            verify { mockCommServer.sendMessage(messageToSend, Recipient.Single(humanGuestConnectionId)) }
            messagesToComputerPlayers.assertEmpty()
        }

        @Test
        fun `Send message to a specific computer guest`() {
            // Given
            val messageToSend = Message.JoinGameAckMessage(GameCommonId(124), DwitchPlayerId(45))

            // When
            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()
            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(computerGuestConnectionId), messageToSend))

            // Then
            verify(exactly = 0) { mockMessageDispatcher.dispatch(EnvelopeReceived(hostConnectionId, messageToSend)) }
            verify(exactly = 0) { mockCommServer.sendMessage(any(), any()) }
            messagesToComputerPlayers.assertValue(EnvelopeToSend(Recipient.Single(computerGuestConnectionId), messageToSend))
        }

        @Test
        fun `Send message to a single recipient that happens to be the host`() {
            // Given
            hostCommunicator.startServer()

            // When
            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()
            val messageToSend = Message.CardsForExchangeMessage(hostPlayerDwitchId, setOf(Card.Clubs2, Card.Clubs3))
            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.Single(hostConnectionId), messageToSend))

            // Then
            verify { mockMessageDispatcher.dispatch(EnvelopeReceived(hostConnectionId, messageToSend)) }
            verify(exactly = 0) { mockCommServer.sendMessage(messageToSend, any()) }
            messagesToComputerPlayers.assertEmpty()
        }

        @Test
        fun `Broadcast message to all guests`() {
            // Given
            val messageToSend = Message.CancelGameMessage

            // When
            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()
            hostCommunicator.sendMessage(EnvelopeToSend(Recipient.All, messageToSend))

            // Then
            verify(exactly = 0) { mockMessageDispatcher.dispatch(EnvelopeReceived(hostConnectionId, messageToSend)) }
            verify { mockCommServer.sendMessage(messageToSend, Recipient.All) }
            messagesToComputerPlayers.assertValue(EnvelopeToSend(Recipient.All, messageToSend))
        }
    }

    @Nested
    inner class SendMessageToHost {

        private val hostPlayerDwitchId = DwitchPlayerId(123)
        private val hostConnectionId = ConnectionStore.hostConnectionId

        @Test
        fun `Messages sent to host are dispatched and not broadcasted or forwarded`() {
            // Given
            hostCommunicator.startServer()

            // When
            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()
            val messageToSend = Message.CardsForExchangeMessage(hostPlayerDwitchId, setOf(Card.Clubs2, Card.Clubs3))
            hostCommunicator.sendMessageToHost(messageToSend)

            // Then
            verify { mockMessageDispatcher.dispatch(EnvelopeReceived(hostConnectionId, messageToSend)) }
            verify(exactly = 0) { mockCommServer.sendMessage(messageToSend, any()) }
            messagesToComputerPlayers.assertEmpty()
        }
    }

    @Nested
    inner class SendMessageToHostFromComputerPlayer {

        private val computerGuest1ConnectionId = ConnectionId(11)
        private val computerGuest2ConnectionId = ConnectionId(12)

        @Test
        fun `Messages sent from computer player to host are dispatched`() {
            // Given
            hostCommunicator.startServer()

            // When
            val messageReceived1 =
                EnvelopeReceived(computerGuest1ConnectionId, Message.PlayerReadyMessage(DwitchPlayerId(12), true))
            val messageReceived2 =
                EnvelopeReceived(computerGuest2ConnectionId, Message.PlayerReadyMessage(DwitchPlayerId(13), false))
            computerCommunicator.sendMessageToHostFromComputerPlayer(messageReceived1)
            computerCommunicator.sendMessageToHostFromComputerPlayer(messageReceived2)

            // Then
            val dispatchedMessageCap = mutableListOf<EnvelopeReceived>()
            verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageCap)) }
            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)
            confirmVerified(mockMessageDispatcher)
        }

        @Test
        fun `Game state update messages received from computer players are forwarded to all guests`() {
            // Given
            hostCommunicator.startServer()

            val messagesToComputerPlayers = computerCommunicator.observeMessagesForComputerPlayers().test()

            // When
            val gameState = TestEntityFactory.createGameState()
            val messageReceived1 = EnvelopeReceived(ConnectionId(1), Message.GameStateUpdatedMessage(gameState))
            val messageReceived2 = EnvelopeReceived(ConnectionId(4), Message.PlayerReadyMessage(DwitchPlayerId(13), false))
            computerCommunicator.sendMessageToHostFromComputerPlayer(messageReceived1)
            computerCommunicator.sendMessageToHostFromComputerPlayer(messageReceived2)

            // Then only GameStateUpdated message is forwarded, PlayerReadyMessage is not
            verify(exactly = 1) { mockCommServer.sendMessage(Message.GameStateUpdatedMessage(gameState), Recipient.All) }
            messagesToComputerPlayers.assertValue(EnvelopeToSend(Recipient.All, Message.GameStateUpdatedMessage(gameState)))
        }
    }

    @Nested
    inner class SendCommunicationEventFromComputerPlayer {

        private val computerGuest1ConnectionId = ConnectionId(11)
        private val computerGuest2ConnectionId = ConnectionId(12)

        @Test
        fun `Communication events sent by computer players are dispatched`() {
            // Given
            hostCommunicator.startServer()

            // When
            computerCommunicator.sendCommunicationEventFromComputerPlayer(
                ServerCommunicationEvent.ClientConnected(
                    computerGuest1ConnectionId
                )
            )
            computerCommunicator.sendCommunicationEventFromComputerPlayer(
                ServerCommunicationEvent.ClientDisconnected(
                    computerGuest2ConnectionId
                )
            )

            // Then
            val dispatchedEventCap = mutableListOf<ServerCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }
            assertThat(dispatchedEventCap[0]).isEqualTo(ServerCommunicationEvent.ClientConnected(computerGuest1ConnectionId))
            assertThat(dispatchedEventCap[1]).isEqualTo(ServerCommunicationEvent.ClientDisconnected(computerGuest2ConnectionId))
            confirmVerified(mockCommunicationEventDispatcher)
        }
    }
}
