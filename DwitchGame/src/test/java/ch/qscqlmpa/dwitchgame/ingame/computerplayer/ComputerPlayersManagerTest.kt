package ch.qscqlmpa.dwitchgame.ingame.computerplayer

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.ServerEvent
import ch.qscqlmpa.dwitchengine.TestDwitchFactory
import ch.qscqlmpa.dwitchengine.computerplayer.ComputerPlayerActionResult
import ch.qscqlmpa.dwitchengine.computerplayer.DwitchComputerPlayerEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.host.ComputerCommunicator
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import io.mockk.*
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ComputerPlayersManagerTest : BaseUnitTest() {

    private val mockCommunicator = mockk<ComputerCommunicator>(relaxed = true)
    private val dwitchFactory = TestDwitchFactory()
    private val mockComputerPlayerEngine = mockk<DwitchComputerPlayerEngine>()

    private lateinit var computerPlayersManager: ComputerPlayersManager

    private lateinit var messagesForComputerPlayersSubject: PublishSubject<EnvelopeToSend>
    private val gameCommonId = GameCommonId(1)
    private val player1 = ComputerPlayer(ConnectionId(1), DwitchPlayerId(1))
    private val player2 = ComputerPlayer(ConnectionId(2), DwitchPlayerId(2))
    private val player3 = ComputerPlayer(ConnectionId(3), DwitchPlayerId(3))
    private val player4 = ComputerPlayer(ConnectionId(4), DwitchPlayerId(4))

    data class ComputerPlayer(val connectionId: ConnectionId, val dwitchId: DwitchPlayerId)

    @BeforeEach
    fun setup() {
        messagesForComputerPlayersSubject = PublishSubject.create()
        every { mockCommunicator.observeMessagesForComputerPlayers() } returns messagesForComputerPlayersSubject
        dwitchFactory.setInstance(mockComputerPlayerEngine)
        computerPlayersManager = ComputerPlayersManager(mockCommunicator, dwitchFactory)
    }

    @Nested
    inner class AddNewPlayer {

        @Test
        fun `Add first computer player to the game`() {
            // Given there is currently no computer player

            // When
            computerPlayersManager.addNewPlayer()

            // Then player connects to server and join game
            verifyOrder {
                mockCommunicator.observeMessagesForComputerPlayers()
                mockCommunicator.sendCommunicationEventFromComputerPlayer(
                    ServerEvent.CommunicationEvent.ClientConnected(
                        player1.connectionId
                    )
                )
                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player1.connectionId,
                        Message.JoinGameMessage("Computer 1", computerManaged = true)
                    )
                )
            }
            confirmVerified(mockCommunicator)
        }

        @Test
        fun `Add second computer player to the game`() {
            // Given there is already one computer player
            computerPlayersManager.addNewPlayer()

            // When
            computerPlayersManager.addNewPlayer()

            // Then player connects to server and join game
            verify(exactly = 1) { mockCommunicator.observeMessagesForComputerPlayers() }
            verify(exactly = 1) {
                mockCommunicator.sendCommunicationEventFromComputerPlayer(
                    ServerEvent.CommunicationEvent.ClientConnected(
                        player2.connectionId
                    )
                )
            }
            verify(exactly = 1) {
                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player2.connectionId,
                        Message.JoinGameMessage("Computer 2", computerManaged = true)
                    )
                )
            }
        }

        @Test
        fun `Player becomes ready when acknowledged by host`() {
            // Given
            computerPlayersManager.addNewPlayer()
            computerPlayersManager.addNewPlayer()

            // When player receives ACK, they become ready
            serverSendsJoinGameAckMessageToComputer(player1)
            serverSendsJoinGameAckMessageToComputer(player2)

            // Then it becomes ready
            verifyOrder {
                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player1.connectionId,
                        Message.PlayerReadyMessage(player1.dwitchId, ready = true)
                    )
                )

                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player2.connectionId,
                        Message.PlayerReadyMessage(player2.dwitchId, ready = true)
                    )
                )
            }
        }
    }

    @Nested
    inner class ResumeExistingPlayer {

        @Test
        fun `Resume first computer player`() {
            // Given there is currently no computer player

            // When
            computerPlayersManager.resumeExistingPlayer(gameCommonId, player1.dwitchId)

            // Then player connects to server and rejoin game
            verifyOrder {
                mockCommunicator.observeMessagesForComputerPlayers()
                mockCommunicator.sendCommunicationEventFromComputerPlayer(
                    ServerEvent.CommunicationEvent.ClientConnected(player1.connectionId)
                )
                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player1.connectionId,
                        Message.RejoinGameMessage(gameCommonId, player1.dwitchId)
                    )
                )
            }
            confirmVerified(mockCommunicator)
        }

        @Test
        fun `Add second computer player to the game`() {
            // Given there is already one computer player
            computerPlayersManager.resumeExistingPlayer(gameCommonId, player1.dwitchId)

            // When
            computerPlayersManager.resumeExistingPlayer(gameCommonId, player2.dwitchId)

            // Then player connects to server and rejoin game
            verify(exactly = 1) { mockCommunicator.observeMessagesForComputerPlayers() }
            verify(exactly = 1) {
                mockCommunicator.sendCommunicationEventFromComputerPlayer(
                    ServerEvent.CommunicationEvent.ClientConnected(player2.connectionId)
                )
            }
            verify(exactly = 1) {
                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player2.connectionId,
                        Message.RejoinGameMessage(gameCommonId, player2.dwitchId)
                    )
                )
            }
        }

        @Test
        fun `Player becomes ready when acknowledged by host`() {
            // Given
            computerPlayersManager.resumeExistingPlayer(gameCommonId, player1.dwitchId)
            computerPlayersManager.resumeExistingPlayer(gameCommonId, player2.dwitchId)

            // When player receives ACK, they become ready
            serverSendsRejoinGameAckMessageToComputer(player1)
            serverSendsRejoinGameAckMessageToComputer(player2)

            // Then it becomes ready
            verifyOrder {
                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player1.connectionId,
                        Message.PlayerReadyMessage(player1.dwitchId, ready = true)
                    )
                )

                mockCommunicator.sendMessageToHostFromComputerPlayer(
                    ServerEvent.EnvelopeReceived(
                        player2.connectionId,
                        Message.PlayerReadyMessage(player2.dwitchId, ready = true)
                    )
                )
            }
        }
    }

    @Nested
    inner class MessageReceived {

        private val receivedGameState = mockk<DwitchGameState>()
        private val updatedGameState = mockk<DwitchGameState>()

        @Test
        fun `When the only enabled computer player is kicked, the processing of messages is stopped`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            assertThat(messagesForComputerPlayersSubject.hasObservers()).isTrue

            // When
            messagesForComputerPlayersSubject.onNext(
                EnvelopeToSend(Recipient.Single(player1.connectionId), Message.KickPlayerMessage(player1.dwitchId))
            )

            // Then
            assertThat(messagesForComputerPlayersSubject.hasObservers()).isFalse
        }

        @Test
        fun `When KickPlayerMessage is received, the corresponding computer player is disabled`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)
            addPlayerAndPerformFullOnboardingProcess(player3)

            // When
            messagesForComputerPlayersSubject.onNext(
                EnvelopeToSend(Recipient.Single(player2.connectionId), Message.KickPlayerMessage(player2.dwitchId))
            )
            addPlayerAndPerformFullOnboardingProcess(player2)
            addPlayerAndPerformFullOnboardingProcess(player4)

            // Then
            val eventsSendToHost = mutableListOf<ServerEvent.CommunicationEvent>()
            verify { mockCommunicator.sendCommunicationEventFromComputerPlayer(capture(eventsSendToHost)) }
            assertThat((eventsSendToHost[0] as ServerEvent.CommunicationEvent.ClientConnected).connectionId).isEqualTo(
                player1.connectionId
            )
            assertThat((eventsSendToHost[1] as ServerEvent.CommunicationEvent.ClientConnected).connectionId).isEqualTo(
                player2.connectionId
            )
            assertThat((eventsSendToHost[2] as ServerEvent.CommunicationEvent.ClientConnected).connectionId).isEqualTo(
                player3.connectionId
            )

            // Connection ID of player2 is recycled for the next player to be added
            assertThat((eventsSendToHost[3] as ServerEvent.CommunicationEvent.ClientConnected).connectionId).isEqualTo(
                player2.connectionId
            )
            assertThat((eventsSendToHost[4] as ServerEvent.CommunicationEvent.ClientConnected).connectionId).isEqualTo(
                player4.connectionId
            )
        }

        @Test
        fun `Cancel game message stops the processing of messages by the computer`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)
            assertThat(messagesForComputerPlayersSubject.hasObservers()).isTrue

            // When
            messagesForComputerPlayersSubject.onNext(EnvelopeToSend(Recipient.All, Message.CancelGameMessage))

            // Then
            assertThat(messagesForComputerPlayersSubject.hasObservers()).isFalse
        }

        @Test
        fun `Game over message stops the processing of messages by the computer`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)
            assertThat(messagesForComputerPlayersSubject.hasObservers()).isTrue

            // When
            messagesForComputerPlayersSubject.onNext(EnvelopeToSend(Recipient.All, Message.GameOverMessage))

            // Then
            assertThat(messagesForComputerPlayersSubject.hasObservers()).isFalse
        }

        @Test
        fun `Launch game message is processed by the relevant player - if any - and the updated game state is sent to host`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)

            // When player1 has performed an action
            every { mockComputerPlayerEngine.handleComputerPlayerAction() } returns listOf(
                ComputerPlayerActionResult(
                    player1.dwitchId,
                    updatedGameState
                )
            )
            messagesForComputerPlayersSubject.onNext(EnvelopeToSend(Recipient.All, Message.LaunchGameMessage(receivedGameState)))

            // Then
            val messagesSent = mutableListOf<ServerEvent.EnvelopeReceived>()
            verify(exactly = 5) { mockCommunicator.sendMessageToHostFromComputerPlayer(capture(messagesSent)) }
            assertThat(messagesSent.size).isEqualTo(5)
            assertThat(messagesSent[0].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[1].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
            assertThat(messagesSent[2].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[3].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
            assertThat(messagesSent[4]).isEqualTo(
                ServerEvent.EnvelopeReceived(
                    player1.connectionId,
                    Message.GameStateUpdatedMessage(updatedGameState)
                )
            )
        }

        @Test
        fun `Launch game message is ignored if no computer player has anything to do with it`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)

            // When no player has anything to do according to the received game state
            every { mockComputerPlayerEngine.handleComputerPlayerAction() } returns emptyList()
            messagesForComputerPlayersSubject.onNext(EnvelopeToSend(Recipient.All, Message.LaunchGameMessage(receivedGameState)))

            // Then nothing is sent back to host
            val messagesSent = mutableListOf<ServerEvent.EnvelopeReceived>()
            verify(exactly = 4) { mockCommunicator.sendMessageToHostFromComputerPlayer(capture(messagesSent)) }
            assertThat(messagesSent.size).isEqualTo(4)
            assertThat(messagesSent[0].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[1].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
            assertThat(messagesSent[2].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[3].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
        }

        @Test
        fun `Game state updated message is processed by the relevant player - if any - and the updated game state is sent to host`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)

            // When player1 has performed an action
            every { mockComputerPlayerEngine.handleComputerPlayerAction() } returns listOf(
                ComputerPlayerActionResult(
                    player1.dwitchId,
                    updatedGameState
                )
            )
            messagesForComputerPlayersSubject.onNext(
                EnvelopeToSend(
                    Recipient.All,
                    Message.GameStateUpdatedMessage(receivedGameState)
                )
            )

            // Then
            val messagesSent = mutableListOf<ServerEvent.EnvelopeReceived>()
            verify(exactly = 5) { mockCommunicator.sendMessageToHostFromComputerPlayer(capture(messagesSent)) }
            assertThat(messagesSent.size).isEqualTo(5)
            assertThat(messagesSent[0].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[1].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
            assertThat(messagesSent[2].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[3].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
            assertThat(messagesSent[4]).isEqualTo(
                ServerEvent.EnvelopeReceived(
                    player1.connectionId,
                    Message.GameStateUpdatedMessage(updatedGameState)
                )
            )
        }

        @Test
        fun `Game state updated message is ignored if no computer player has anything to do with it`() {
            // Given
            addPlayerAndPerformFullOnboardingProcess(player1)
            addPlayerAndPerformFullOnboardingProcess(player2)

            // When no player has anything to do according to the received game state
            every { mockComputerPlayerEngine.handleComputerPlayerAction() } returns emptyList()
            messagesForComputerPlayersSubject.onNext(
                EnvelopeToSend(
                    Recipient.All,
                    Message.GameStateUpdatedMessage(receivedGameState)
                )
            )

            // Then nothing is sent back to host
            val messagesSent = mutableListOf<ServerEvent.EnvelopeReceived>()
            verify(exactly = 4) { mockCommunicator.sendMessageToHostFromComputerPlayer(capture(messagesSent)) }
            assertThat(messagesSent.size).isEqualTo(4)
            assertThat(messagesSent[0].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[1].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
            assertThat(messagesSent[2].message).isInstanceOf(Message.JoinGameMessage::class.java)
            assertThat(messagesSent[3].message).isInstanceOf(Message.PlayerReadyMessage::class.java)
        }
    }

    private fun serverSendsJoinGameAckMessageToComputer(player: ComputerPlayer) {
        messagesForComputerPlayersSubject.onNext(
            EnvelopeToSend(
                Recipient.Single(player.connectionId), Message.JoinGameAckMessage(gameCommonId, player.dwitchId)
            )
        )
    }

    private fun serverSendsRejoinGameAckMessageToComputer(player: ComputerPlayer) {
        messagesForComputerPlayersSubject.onNext(
            EnvelopeToSend(
                Recipient.Single(player.connectionId),
                Message.RejoinGameAckMessage(gameCommonId, RoomType.WAITING_ROOM, player.dwitchId)
            )
        )
    }

    private fun addPlayerAndPerformFullOnboardingProcess(player: ComputerPlayer) {
        computerPlayersManager.addNewPlayer()
        serverSendsJoinGameAckMessageToComputer(player)
    }
}
