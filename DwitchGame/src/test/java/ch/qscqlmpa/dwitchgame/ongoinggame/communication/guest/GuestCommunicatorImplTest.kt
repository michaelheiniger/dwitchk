package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors.MessageDispatcher
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GuestCommunicatorImplTest : BaseUnitTest() {

    private val mockCommClient = mockk<CommClient>(relaxed = true)
    private val mockMessageDispatcher = mockk<MessageDispatcher>(relaxed = true)
    private val mockCommunicationEventDispatcher = mockk<GuestCommunicationEventDispatcher>(relaxed = true)
    private val mockCommEventRepository = mockk<GuestCommunicationStateRepository>(relaxed = true)

    private lateinit var guestCommunicator: GuestCommunicator

    private lateinit var communicationEventsSubject: PublishSubject<ClientCommunicationEvent>
    private lateinit var receivedMessagesSubject: PublishSubject<EnvelopeReceived>

    @BeforeEach
    fun setup() {
        guestCommunicator = GuestCommunicatorImpl(
            mockCommClient,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            TestSchedulerFactory(),
            mockk(relaxed = true)
        )

        communicationEventsSubject = PublishSubject.create()
        every { mockCommClient.observeCommunicationEvents() } returns communicationEventsSubject

        receivedMessagesSubject = PublishSubject.create()
        every { mockCommClient.observeReceivedMessages() } returns receivedMessagesSubject

        every { mockCommunicationEventDispatcher.dispatch(any()) } returns Completable.complete()
    }

    /**
     * connect
     * disconnect
     * sendMessageToHost
     */

    @Nested
    inner class Connect {

        @Test
        fun `Start communication client operations`() {
            // Given
            assertThat(communicationEventsSubject.hasObservers()).isFalse
            assertThat(receivedMessagesSubject.hasObservers()).isFalse

            // When
            guestCommunicator.connect()

            // Then
            assertThat(communicationEventsSubject.hasObservers()).isTrue
            assertThat(receivedMessagesSubject.hasObservers()).isTrue

            verifyOrder {
                mockCommClient.observeCommunicationEvents()
                mockCommClient.observeReceivedMessages()
                mockCommClient.start()
            }
            confirmVerified(mockCommClient)
        }

        @Test
        fun `Communication events emitted by communication client are dispatched`() {
            // Given
            guestCommunicator.connect()

            // When
            communicationEventsSubject.onNext(ClientCommunicationEvent.ConnectedToHost)
            communicationEventsSubject.onNext(ClientCommunicationEvent.DisconnectedFromHost)

            // Then
            val dispatchedEventCap = mutableListOf<ClientCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }
            assertThat(dispatchedEventCap[0]).isEqualTo(ClientCommunicationEvent.ConnectedToHost)
            assertThat(dispatchedEventCap[1]).isEqualTo(ClientCommunicationEvent.DisconnectedFromHost)
            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Messages received through communication client are dispatched`() {
            // Given
            guestCommunicator.connect()

            // When
            val messageReceived1 = mockk<EnvelopeReceived>()
            val messageReceived2 = mockk<EnvelopeReceived>()
            receivedMessagesSubject.onNext(messageReceived1)
            receivedMessagesSubject.onNext(messageReceived2)

            // Then
            val dispatchedMessageCap = mutableListOf<EnvelopeReceived>()
            verify(exactly = 2) { mockMessageDispatcher.dispatch(capture(dispatchedMessageCap)) }
            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)
            confirmVerified(mockMessageDispatcher)
        }
    }

    @Nested
    inner class Disconnect {

        @Test
        fun `Client communication is disconnected from the host and messages and communication events are no longer processed`() {
            // Given
            guestCommunicator.connect()
            assertThat(communicationEventsSubject.hasObservers()).isTrue
            assertThat(receivedMessagesSubject.hasObservers()).isTrue

            // When
            guestCommunicator.disconnect()
            communicationEventsSubject.onNext(ClientCommunicationEvent.DisconnectedFromHost)

            // Then
            assertThat(communicationEventsSubject.hasObservers()).isFalse // Observed streams have been disposed.
            assertThat(receivedMessagesSubject.hasObservers()).isFalse // Observed streams have been disposed.

            verifyOrder {
                mockCommClient.observeCommunicationEvents()
                mockCommClient.observeReceivedMessages()
                mockCommClient.start()
                mockCommClient.stop()
            }
            confirmVerified(mockCommClient)
        }
    }

    @Nested
    inner class SendMessageToHost {

        @Test
        fun `Send message to the host`() {
            // Given
            val messageToSend = Message.PlayerReadyMessage(DwitchPlayerId(2), true)

            // When
            guestCommunicator.sendMessageToHost(messageToSend)

            // Then
            val messageCap = CapturingSlot<Message>()
            verify { mockCommClient.sendMessageToServer(capture(messageCap)) }
            val messageSentCap = messageCap.captured as Message.PlayerReadyMessage
            assertThat(messageSentCap).isEqualTo(messageToSend)
        }
    }
}
