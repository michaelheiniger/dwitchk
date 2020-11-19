package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GuestConnectedToHostEventProcessorTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var commEventRepository: GuestCommunicationEventRepository

    private lateinit var processorGuest: GuestConnectedToHostEventProcessor

    private lateinit var game: Game
    private lateinit var localPlayer: Player

    @BeforeEach
    override fun setup() {
        super.setup()
        commEventRepository = GuestCommunicationEventRepository()
        processorGuest = GuestConnectedToHostEventProcessor(
            mockInGameStore,
            mockCommunicator,
            commEventRepository
        )
        setupCommunicatorMock()
    }

    @Test
    @DisplayName("Send JoinGameMessage because registration with host has not been done yet (in-game ID is 0)")
    fun `Send JoinGameMessage`() {
        setupTest(PlayerInGameId(0))

        launchTest()

        verify {
            mockCommunicator.sendMessage(
                EnvelopeToSend(
                    RecipientType.All,
                    Message.JoinGameMessage(localPlayer.name)
                )
            )
        }
        confirmVerified(mockCommunicator)
        assertCommunicationStateIsNowConnected()
    }

    @Test
    @DisplayName("Send RejoinGameMessage because registration with host has already been done (in-game ID is not 0)")
    fun `Send RejoinGameMessage`() {
        setupTest(PlayerInGameId(23))

        launchTest()

        verify {
            mockCommunicator.sendMessage(
                EnvelopeToSend(
                    RecipientType.All,
                    Message.RejoinGameMessage(game.gameCommonId, localPlayer.inGameId)
                )
            )
        }
        confirmVerified(mockCommunicator)
        assertCommunicationStateIsNowConnected()
    }

    private fun launchTest() {
        processorGuest.process(ClientCommunicationEvent.ConnectedToHost).test().assertComplete()
    }

    private fun assertCommunicationStateIsNowConnected() {
        assertThat(commEventRepository.consumeLastEvent()).isEqualTo(GuestCommunicationState.Connected)
    }

    private fun setupTest(localPlayerInGameId: PlayerInGameId) {
        setupPlayer(localPlayerInGameId)
        setupGetPlayerMock()
        setupGame()
    }

    private fun setupPlayer(localPlayerInGameId: PlayerInGameId) {
        localPlayer = TestEntityFactory.createGuestPlayer1().copy(inGameId = localPlayerInGameId)
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