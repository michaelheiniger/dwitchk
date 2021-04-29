package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.joda.time.LocalTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NewGameUsecaseTest : BaseUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private lateinit var newGameUsecase: NewGameUsecase

    @BeforeEach
    fun setup() {
        newGameUsecase = NewGameUsecase(mockAppEventRepository, mockStore)
    }

    @Nested
    inner class Host {

        private val gameLocalId = 1L
        private val gameCommonId = GameCommonId(123L)
        private val gameName = "Kaamelott"
        private val gamePort = 8889
        private val localPlayerLocalId = 10L
        private val playerName = "Arthur"

        @BeforeEach
        fun setup() {
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
                mockAppEventRepository.notify(
                    AppEvent.GameCreated(
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
            newGameUsecase.hostGame(gameName, playerName, gamePort).test().assertComplete()
        }
    }

    @Nested
    inner class Guest {

        private val gameLocalId = 1L
        private val gameCommonId = GameCommonId(123L)
        private val gameName = "Kaamelott"
        private val gamePort = 8889
        private val localPlayerLocalId = 10L
        private val gameIpAddress = "192.168.1.1"
        private val advertisedGame = AdvertisedGame(true, gameName, gameCommonId, gameIpAddress, gamePort, LocalTime.now())
        private val playerName = "Lancelot"

        @BeforeEach
        fun setup() {
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
                mockAppEventRepository.notify(
                    AppEvent.GameJoined(GameJoinedInfo(gameLocalId, localPlayerLocalId, gameIpAddress, gamePort))
                )
            }
        }
    }
}
