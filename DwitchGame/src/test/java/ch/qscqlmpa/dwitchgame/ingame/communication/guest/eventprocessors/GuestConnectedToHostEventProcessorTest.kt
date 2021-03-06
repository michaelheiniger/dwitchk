package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    fun setup() {
        commStateRepository = GuestCommunicationStateRepository()
        processorGuest = GuestConnectedToHostEventProcessor(
            mockInGameStore,
            mockCommunicator,
            commStateRepository
        )
    }

    @Test
    @DisplayName("Send JoinGameMessage because registration with host has not been done yet (in-game ID is 0)")
    fun `Send JoinGameMessage`() {
        setupTest(DwitchPlayerId(0))

        launchTest()

        verify { mockCommunicator.sendMessageToHost(Message.JoinGameMessage(localPlayer.name)) }
        confirmVerified(mockCommunicator)
        assertCommunicationStateIsNowConnected()
    }

    @Test
    @DisplayName("Send RejoinGameMessage because registration with host has already been done (in-game ID is not 0)")
    fun `Send RejoinGameMessage`() {
        setupTest(DwitchPlayerId(23))

        launchTest()

        verify { mockCommunicator.sendMessageToHost(Message.RejoinGameMessage(game.gameCommonId, localPlayer.dwitchId)) }
        confirmVerified(mockCommunicator)
        assertCommunicationStateIsNowConnected()
    }

    private fun launchTest() {
        processorGuest.process(ClientCommunicationEvent.ConnectedToHost).test().assertComplete()
    }

    private fun assertCommunicationStateIsNowConnected() {
        assertThat(commStateRepository.currentState().blockingFirst()).isEqualTo(GuestCommunicationState.Connected)
    }

    private fun setupTest(localPlayerDwitchId: DwitchPlayerId) {
        setupPlayer(localPlayerDwitchId)
        setupGetPlayerMock()
        setupGame()
    }

    private fun setupPlayer(localPlayerDwitchId: DwitchPlayerId) {
        localPlayer = TestEntityFactory.createGuestPlayer1().copy(dwitchId = localPlayerDwitchId)
    }

    private fun setupGame() {
        game = TestEntityFactory.createGameInWaitingRoom(localPlayer.id).copy()
        every { mockInGameStore.getGame() } returns game
    }

    private fun setupGetPlayerMock() {
        every { mockInGameStore.getLocalPlayer() } returns localPlayer
    }
}
