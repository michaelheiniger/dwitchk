package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerReadyUsecaseTest : BaseUnitTest() {

    private val mockCommunicator = mockk<GuestCommunicator>(relaxed = true)

    private lateinit var usecase: PlayerReadyUsecase

    private val localPlayerInGameId = PlayerInGameId(23)

    @BeforeEach
    override fun setup() {
        super.setup()

        usecase = PlayerReadyUsecase(mockInGameStore, mockCommunicator)

        every { mockCommunicator.sendMessage(any()) } returns Completable.complete()
        every { mockInGameStore.getLocalPlayerInGameId() } returns localPlayerInGameId
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

        verify { mockInGameStore.updatePlayerWithReady(localPlayerInGameId, state) }

        val messageSentCap = CapturingSlot<EnvelopeToSend>()
        verify { mockCommunicator.sendMessage(capture(messageSentCap)) }

        val messageSent = messageSentCap.captured
        assertThat(messageSent.recipient).isEqualTo(RecipientType.All)
        assertThat(messageSent.message).isEqualTo(Message.PlayerReadyMessage(localPlayerInGameId, state))
    }
}