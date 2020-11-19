package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
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


class GuestCommunicatorImplTest : BaseUnitTest() {

    private val mockCommClient = mockk<CommClient>(relaxed = true)

    private val mockMessageDispatcher = mockk<MessageDispatcher>(relaxed = true)

    private val mockCommunicationEventDispatcher = mockk<GuestCommunicationEventDispatcher>(relaxed = true)

    private val mockCommEventRepository = mockk<GuestCommunicationEventRepository>(relaxed = true)

    private lateinit var guestCommunicator: GuestCommunicator

    private lateinit var communicationEventsStream: PublishSubject<ClientCommunicationEvent>

    private lateinit var receivedMessagesStream: PublishSubject<EnvelopeReceived>

    @BeforeEach
    override fun setup() {
        super.setup()

        guestCommunicator = GuestCommunicatorImpl(
            mockCommClient,
            mockMessageDispatcher,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            TestSchedulerFactory()
        )

        communicationEventsStream = PublishSubject.create()
        every { mockCommClient.observeCommunicationEvents() } returns communicationEventsStream

        receivedMessagesStream = PublishSubject.create()
        every { mockCommClient.observeReceivedMessages() } returns receivedMessagesStream
    }

    @Nested
    inner class Connect {

        @Test
        fun `Setup connection with host`() {
            assertThat(communicationEventsStream.hasObservers()).isFalse

            guestCommunicator.connect()
            assertThat(communicationEventsStream.hasObservers()).isTrue

            verifyOrder {
                mockCommClient.observeCommunicationEvents()
                mockCommClient.observeReceivedMessages()
                mockCommClient.start()
            }
            confirmVerified(mockCommClient)
        }

        @Test
        fun `Communication events emitted by websocket client are dispatched`() {
            guestCommunicator.connect()

            communicationEventsStream.onNext(ClientCommunicationEvent.ConnectedToHost)
            communicationEventsStream.onNext(ClientCommunicationEvent.DisconnectedFromHost)

            val dispatchedEventCap = mutableListOf<ClientCommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }

            assertThat(dispatchedEventCap[0]).isEqualTo(ClientCommunicationEvent.ConnectedToHost)
            assertThat(dispatchedEventCap[1]).isEqualTo(ClientCommunicationEvent.DisconnectedFromHost)

            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Received messages emitted by websocket client are dispatched`() {
            guestCommunicator.connect()

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
    inner class CloseConnection {

        @Test
        fun `Close connection with host`() {
            guestCommunicator.connect()

            guestCommunicator.closeConnection()
            assertThat(communicationEventsStream.hasObservers()).isFalse // Observed streams have been disposed.

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
    inner class SendMessage {

        @Test
        fun `Send message`() {
            every { mockCommClient.sendMessage(any()) } returns Completable.complete()
            val messageToSend = Message.PlayerReadyMessage(PlayerInGameId(2), true)

            guestCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, messageToSend)).test().assertComplete()

            val messageCap = CapturingSlot<Message>()
            verify { mockCommClient.sendMessage(capture(messageCap)) }

            val messageSentCap = messageCap.captured as Message.PlayerReadyMessage
            assertThat(messageSentCap).isEqualTo(messageToSend)
        }
    }

    @Nested
    inner class ObserveCommunicationState {

        @Test
        fun `Communication events emitted by the repository are simply forwarded`() {
            every { mockCommEventRepository.observeEvents() } returns Observable.just(GuestCommunicationState.Connected)

            guestCommunicator.observeCommunicationState().test().assertValue(GuestCommunicationState.Connected)
        }
    }
}