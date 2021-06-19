package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerReadyUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var usecase: PlayerReadyUsecase

    private val localPlayerDwitchId = DwitchPlayerId(23)

    @BeforeEach
    fun setup() {
        usecase = PlayerReadyUsecase(mockInGameStore, mockCommunicator)
        every { mockInGameStore.getLocalPlayerDwitchId() } returns localPlayerDwitchId
    }

    @Test
    fun `Player ready state is updated to true`() {
        playerReadyStateIsUpdated(true)
    }

    @Test
    fun `Player ready state is updated to false`() {
        playerReadyStateIsUpdated(false)
    }

    private fun playerReadyStateIsUpdated(state: Boolean) {
        usecase.updateReadyState(state).test().assertComplete()

        verify { mockInGameStore.updatePlayerWithReady(localPlayerDwitchId, state) }
        verify { mockCommunicator.sendMessageToHost(Message.PlayerReadyMessage(localPlayerDwitchId, state)) }
    }
}
