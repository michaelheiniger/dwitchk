package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.ingame.computerplayer.ComputerPlayersManager
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.ingamestore.model.ResumeComputerPlayersInfo
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResumeComputerPlayersUsecaseTest : BaseUnitTest() {

    private val mockComputer = mockk<ComputerPlayersManager>()

    private lateinit var usecase: ResumeComputerPlayersUsecase

    @BeforeEach
    fun setup() {
        usecase = ResumeComputerPlayersUsecase(mockInGameStore, mockComputer)
    }

    @Test
    fun `computer players are resumed`() {
        val playersId = listOf(DwitchPlayerId(1), DwitchPlayerId(2))
        val gameCommonId = GameCommonId(324)
        every { mockInGameStore.getComputerPlayersToResume() } returns ResumeComputerPlayersInfo(gameCommonId, playersId)
        every { mockComputer.resumeExistingPlayer(any(), any()) } just runs

        usecase.resumeComputerPlayers()

        verify { mockComputer.resumeExistingPlayer(gameCommonId, DwitchPlayerId(1)) }
        verify { mockComputer.resumeExistingPlayer(gameCommonId, DwitchPlayerId(2)) }
        confirmVerified(mockComputer)
    }
}
