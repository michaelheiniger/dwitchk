package ch.qscqlmpa.dwitchgame.ingame.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.GameStateUpdatedMessageProcessor
import ch.qscqlmpa.dwitchstore.model.Player
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GameStateUpdatedMessageProcessorTest : BaseMessageProcessorTest() {

    private val gameState: DwitchGameState = TestEntityFactory.createGameState()

    private lateinit var localPlayer: Player

    private lateinit var processor: GameStateUpdatedMessageProcessor

    @BeforeEach
    fun setup() {
        processor = GameStateUpdatedMessageProcessor(mockInGameStore)
    }

    @Test
    fun `Updated store with game state from message`() {
        localPlayer = TestEntityFactory.createGuestPlayer1()
        every { mockInGameStore.getLocalPlayer() } returns localPlayer

        launchTest()

        verify { mockInGameStore.updateGameState(gameState) }
        confirmVerified(mockInGameStore)
    }

    private fun launchTest() {
        processor.process(Message.GameStateUpdatedMessage(gameState), ConnectionId(0)).test().assertComplete()
    }
}
