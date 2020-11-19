package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
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