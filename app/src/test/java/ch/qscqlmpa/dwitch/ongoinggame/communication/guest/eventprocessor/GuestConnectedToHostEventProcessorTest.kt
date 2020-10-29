package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessor

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ConnectedToHost
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors.GuestConnectedToHostEventProcessor
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
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
    inner class Process {

        private lateinit var game: Game
        private lateinit var localPlayer: Player

        @BeforeEach
        fun setup() {
            setupCommunicatorMock()
        }

        @Test
        @DisplayName("Send JoinGameMessage because registration with host has not been done yet (in-game ID is 0)")
        fun `Send JoinGameMessage`() {
            setupTest(PlayerInGameId(0))
            val joinGameMessageMessage = Message.JoinGameMessage(localPlayer.name)

            processorGuest.process(ConnectedToHost).test().assertComplete()

            verify { mockCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, joinGameMessageMessage)) }
            confirmVerified(mockCommunicator)
        }

        @Test
        @DisplayName("Send RejoinGameMessage because registration with host has already been done (in-game ID is not 0)")
        fun `Send RejoinGameMessage`() {
            setupTest(PlayerInGameId(23))
            val rejoinGameMessage = Message.RejoinGameMessage(localPlayer.inGameId)

            processorGuest.process(ConnectedToHost).test().assertComplete()

            verify { mockCommunicator.sendMessage(EnvelopeToSend(RecipientType.All, rejoinGameMessage)) }
            confirmVerified(mockCommunicator)
        }

        private fun setupTest(localPlayerInGameId: PlayerInGameId) {
            setupPlayer(localPlayerInGameId)
            setupGetPlayerMock()
            setupGame()
        }

        private fun setupPlayer(localPlayerInGameId: PlayerInGameId) {
            localPlayer = TestEntityFactory.createGuestPlayer1()
                .copy(inGameId = localPlayerInGameId)
        }

        private fun setupGame() {
            game = TestEntityFactory.createGameInWaitingRoom(localPlayer.id).copy()
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