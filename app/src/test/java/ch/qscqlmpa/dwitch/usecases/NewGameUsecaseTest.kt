package ch.qscqlmpa.dwitch.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.gameadvertising.GameInfo
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.model.InsertGameResult
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.mockk.*
import org.joda.time.LocalTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NewGameUsecaseTest : BaseUnitTest() {

    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)

    private lateinit var newGameUsecase: NewGameUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        newGameUsecase = NewGameUsecase(mockServiceManager, mockStore)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockServiceManager)
    }

    @Nested
    inner class Host {

        private val gameLocalId = 1L
        private val gameCommonId = GameCommonId(123L)
        private val gameName = "Kaamelott"
        private val gameInfo = GameInfo(gameCommonId, gameName, 8889)
        private val localPlayerLocalId = 10L
        private val playerName = "Arthur"

        @BeforeEach
        fun setup() {
            every { mockServiceManager.startHostService(any(), any(), any()) } just Runs
            every { mockStore.insertGameForHost(any(), any()) } returns
                    InsertGameResult(gameLocalId, gameCommonId, gameName, localPlayerLocalId)
        }

        @Test
        fun `should create game in store`() {
            newGameUsecase.hostNewGame(gameName, playerName).test().assertComplete()

            verify { mockStore.insertGameForHost(gameName, playerName) }
        }

        @Test
        fun `should start service`() {
            newGameUsecase.hostNewGame(gameName, playerName).test().assertComplete()

            verify {
                mockServiceManager.startHostService(
                    gameLocalId,
                    gameInfo,
                    localPlayerLocalId
                )
            }
        }
    }

    @Nested
    inner class Guest {

        private val gameLocalId = 1L
        private val gameCommonId = GameCommonId(123L)
        private val gameName = "Kaamelott"
        private val gamePort = 8889
        private val localPlayerLocalId = 10L
        private val hostIpAddress = "192.168.1.1"
        private val advertisedGame =
            AdvertisedGame(gameName, gameCommonId, hostIpAddress, gamePort, LocalTime.now())
        private val playerName = "Lancelot"

        @BeforeEach
        fun setup() {
            every { mockServiceManager.startGuestService(any(), any(), any(), any()) } just Runs
            every { mockStore.insertGameForGuest(any(), any(), any()) } returns
                    InsertGameResult(gameLocalId, gameCommonId, gameName, localPlayerLocalId)
        }

        @Test
        fun `should create game in store`() {
            newGameUsecase.joinGame(advertisedGame, playerName).test().assertComplete()

            verify { mockStore.insertGameForGuest(gameName, gameCommonId, playerName) }
        }

        @Test
        fun `should start service`() {
            newGameUsecase.joinGame(advertisedGame, playerName).test().assertComplete()

            verify {
                mockServiceManager.startGuestService(
                    gameLocalId,
                    localPlayerLocalId,
                    gamePort,
                    advertisedGame.gameIpAddress
                )
            }
        }
    }
}