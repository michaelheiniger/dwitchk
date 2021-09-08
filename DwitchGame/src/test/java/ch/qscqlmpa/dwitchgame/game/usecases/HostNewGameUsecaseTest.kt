package ch.qscqlmpa.dwitchgame.game.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HostNewGameUsecaseTest : BaseUnitTest() {

    private val mockGameLifecycleEventRepository = mockk<HostGameLifecycleEventRepository>(relaxed = true)

    private lateinit var hostNewGameUsecase: HostNewGameUsecase

    private val gameLocalId = 1L
    private val gameCommonId = GameCommonId(123L)
    private val gameName = "Kaamelott"
    private val localPlayerLocalId = 10L
    private val playerName = "Arthur"

    @BeforeEach
    fun setup() {
        hostNewGameUsecase = HostNewGameUsecase(mockGameLifecycleEventRepository, mockStore)
        every { mockStore.insertGameForHost(any(), any()) } returns
            InsertGameResult(gameLocalId, gameCommonId, gameName, localPlayerLocalId)
    }

    @Test
    fun `should create game in store`() {
        // Given initial state (nothing to setup)

        // When
        hostNewGameUsecase.hostGame(gameName, playerName).test().assertComplete()

        // Then
        verify { mockStore.insertGameForHost(gameName, playerName) }
    }

    @Test
    fun `should notify that the game has been setup`() {
        // Given initial state (nothing to setup)

        // When
        hostNewGameUsecase.hostGame(gameName, playerName).test().assertComplete()

        // Then
        verify {
            mockGameLifecycleEventRepository.notify(
                HostGameLifecycleEvent.GameSetup(
                    GameCreatedInfo(
                        isNew = true,
                        gameLocalId,
                        gameCommonId,
                        gameName,
                        localPlayerLocalId
                    )
                )
            )
        }
    }
}
