package ch.qscqlmpa.dwitchgame.ingame.communication.guest

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.CommClient
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors.GuestCommunicationEventDispatcher
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class GuestCommunicatorImplTest : BaseUnitTest() {

    private val mockCommClient = mockk<CommClient>(relaxed = true)
    private val mockCommunicationEventDispatcher = mockk<GuestCommunicationEventDispatcher>(relaxed = true)
    private val mockCommEventRepository = mockk<GuestCommunicationStateRepository>(relaxed = true)

    private lateinit var guestCommunicator: GuestCommunicator

    private lateinit var communicationEventsSubject: PublishSubject<ClientEvent>

    private val advertisedGame = AdvertisedGame(
        isNew = true,
        gameName = "LOTR",
        gameCommonId = GameCommonId(UUID.randomUUID()),
        gameIpAddress = "192.168.1.2",
        gamePort = 8889,
    )

    @BeforeEach
    fun setup() {
        guestCommunicator = GuestCommunicatorImpl(
            advertisedGame,
            mockCommClient,
            mockCommunicationEventDispatcher,
            mockCommEventRepository,
            TestSchedulerFactory()
        )

        communicationEventsSubject = PublishSubject.create()
        every { mockCommClient.observeCommunicationEvents() } returns communicationEventsSubject

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

            // When
            guestCommunicator.connect()

            // Then
            assertThat(communicationEventsSubject.hasObservers()).isTrue

            verifyOrder {
                mockCommClient.observeCommunicationEvents()
                mockCommClient.start(advertisedGame.gameIpAddress, advertisedGame.gamePort)
            }
            confirmVerified(mockCommClient)
        }

        @Test
        fun `Communication events emitted by communication client are dispatched`() {
            // Given
            guestCommunicator.connect()

            // When
            communicationEventsSubject.onNext(ClientEvent.CommunicationEvent.ConnectedToHost)
            communicationEventsSubject.onNext(ClientEvent.CommunicationEvent.DisconnectedFromHost)

            // Then
            val dispatchedEventCap = mutableListOf<ClientEvent.CommunicationEvent>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedEventCap)) }
            assertThat(dispatchedEventCap[0]).isEqualTo(ClientEvent.CommunicationEvent.ConnectedToHost)
            assertThat(dispatchedEventCap[1]).isEqualTo(ClientEvent.CommunicationEvent.DisconnectedFromHost)
            confirmVerified(mockCommunicationEventDispatcher)
        }

        @Test
        fun `Messages received through communication client are dispatched`() {
            // Given
            guestCommunicator.connect()

            // When
            val messageReceived1 = mockk<ClientEvent.EnvelopeReceived>()
            val messageReceived2 = mockk<ClientEvent.EnvelopeReceived>()
            communicationEventsSubject.onNext(messageReceived1)
            communicationEventsSubject.onNext(messageReceived2)

            // Then
            val dispatchedMessageCap = mutableListOf<ClientEvent.EnvelopeReceived>()
            verify(exactly = 2) { mockCommunicationEventDispatcher.dispatch(capture(dispatchedMessageCap)) }
            assertThat(dispatchedMessageCap[0]).isEqualTo(messageReceived1)
            assertThat(dispatchedMessageCap[1]).isEqualTo(messageReceived2)
            confirmVerified(mockCommunicationEventDispatcher)
        }
    }

    @Nested
    inner class Disconnect {

        @Test
        fun `Client communication is disconnected from the host and messages and communication events are no longer processed`() {
            // Given
            guestCommunicator.connect()
            assertThat(communicationEventsSubject.hasObservers()).isTrue

            // When
            guestCommunicator.disconnect()
            communicationEventsSubject.onNext(ClientEvent.CommunicationEvent.DisconnectedFromHost)

            // Then
            assertThat(communicationEventsSubject.hasObservers()).isFalse // Observed streams have been disposed.

            verifyOrder {
                mockCommClient.observeCommunicationEvents()
                mockCommClient.start(advertisedGame.gameIpAddress, advertisedGame.gamePort)
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
