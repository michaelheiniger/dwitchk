package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessor

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ConnectedToHost
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestConnectedToHostEventProcessor
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GuestConnectedToHostEventProcessorTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var processorGuest: GuestConnectedToHostEventProcessor

    @BeforeEach
    override fun setup() {
        super.setup()
        processorGuest = GuestConnectedToHostEventProcessor(mockInGameStore, mockCommunicator)
    }

    @Nested
    @DisplayName("process()")
    inner class Process {

        private lateinit var game: Game
        private val localPlayer = TestEntityFactory.createGuestPlayer1()

        @BeforeEach
        fun setup() {
            setupCommunicatorMock()
            setupGetPlayerMock()
        }

        @Test
        @DisplayName("send JoinGameMessage because registration with host has not been done yet (gameCommonId is 0)")
        fun `send JoinGameMessage`() {
            setupGame(0)

            val joinGameMessageMessage = Message.JoinGameMessage(localPlayer.name)

            processorGuest.process(ConnectedToHost).test().assertComplete()

            verify { mockCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, joinGameMessageMessage)) }
            confirmVerified(mockCommunicator)
        }

        @Test
        @DisplayName("send RejoinGameMessage because registration with host has already been done (gameCommonId is not 0)")
        fun `send RejoinGameMessage`() {
            setupGame(5)

            val rejoinGameMessage = Message.RejoinGameMessage(localPlayer.inGameId)

            processorGuest.process(ConnectedToHost).test().assertComplete()

            verify { mockCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, rejoinGameMessage)) }
            confirmVerified(mockCommunicator)
        }

        private fun setupGame(gameCommonId: Long) {
            game = TestEntityFactory.createGameInWaitingRoom(localPlayer.id).copy(gameCommonId = gameCommonId)
            every { mockInGameStore.getGame() } returns game
        }

        private fun setupGetPlayerMock() {
            every { mockInGameStore.getLocalPlayer() } returns localPlayer
        }

        private fun setupCommunicatorMock() {
            every { mockCommunicator.sendMessage((any())) } returns Completable.complete()
        }
    }
}