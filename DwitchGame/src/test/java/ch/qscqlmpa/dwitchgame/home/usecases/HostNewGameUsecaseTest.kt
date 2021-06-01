package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
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
    private val gamePort = 8889
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
        launchTest()

        verify { mockStore.insertGameForHost(gameName, playerName) }
    }

    @Test
    fun `should start service`() {
        launchTest()

        verify {
            mockGameLifecycleEventRepository.notify(
                HostGameLifecycleEvent.GameCreated(
                    GameCreatedInfo(
                        true,
                        gameLocalId,
                        gameCommonId,
                        gameName,
                        localPlayerLocalId,
                        gamePort
                    )
                )
            )
        }
    }

    private fun launchTest() {
        hostNewGameUsecase.hostGame(gameName, playerName, gamePort).test().assertComplete()
    }
}