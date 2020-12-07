package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchmodel.game.Game
import ch.qscqlmpa.dwitchmodel.player.Player
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GuestConnectedToHostEventProcessorTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var commStateRepository: GuestCommunicationStateRepository

    private lateinit var processorGuest: GuestConnectedToHostEventProcessor

    private lateinit var game: Game
    private lateinit var localPlayer: Player

    @BeforeEach
    override fun setup() {
        super.setup()
        commStateRepository = GuestCommunicationStateRepository()
        processorGuest = GuestConnectedToHostEventProcessor(
            mockInGameStore,
            mockCommunicator,
            commStateRepository
        )
        setupCommunicatorMock()
    }

    @Test
    @DisplayName("Send JoinGameMessage because registration with host has not been done yet (in-game ID is 0)")
    fun `Send JoinGameMessage`() {
        setupTest(PlayerInGameId(0))

        launchTest()

        verify { mockCommunicator.sendMessageToHost(Message.JoinGameMessage(localPlayer.name)) }
        confirmVerified(mockCommunicator)
        assertCommunicationStateIsNowConnected()
    }

    @Test
    @DisplayName("Send RejoinGameMessage because registration with host has already been done (in-game ID is not 0)")
    fun `Send RejoinGameMessage`() {
        setupTest(PlayerInGameId(23))

        launchTest()

        verify { mockCommunicator.sendMessageToHost(Message.RejoinGameMessage(game.gameCommonId, localPlayer.inGameId)) }
        confirmVerified(mockCommunicator)
        assertCommunicationStateIsNowConnected()
    }

    private fun launchTest() {
        processorGuest.process(ClientCommunicationEvent.ConnectedToHost).test().assertComplete()
    }

    private fun assertCommunicationStateIsNowConnected() {
        assertThat(commStateRepository.observeEvents().blockingFirst()).isEqualTo(GuestCommunicationState.Connected)
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
        every { mockCommunicator.sendMessageToHost((any())) } returns Completable.complete()
    }
}