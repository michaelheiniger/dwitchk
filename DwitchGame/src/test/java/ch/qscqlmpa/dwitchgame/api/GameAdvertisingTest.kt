package ch.qscqlmpa.dwitchgame.api

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchgame.common.testApplicationConfig
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertising
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingImpl
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.gameadvertising.network.Network
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit

class GameAdvertisingTest : BaseUnitTest() {

    private val mockApplicationConfigRepository = mockk<ApplicationConfigRepository>(relaxed = true)
    private val mockNetwork = mockk<Network>(relaxed = true)
    private lateinit var timeScheduler: TestScheduler

    private lateinit var gameAdvertising: GameAdvertising

    private val gameCommonId = "a06ef013-5788-4fd4-adad-aa90a2da8c7c"
    private val expectedAdvertising =
        "{\"isNew\":true,\"gameCommonId\":\"$gameCommonId\",\"gameName\":\"gameName\",\"gamePort\":8889}"

    @BeforeEach
    fun setup() {
        every { mockApplicationConfigRepository.config } returns testApplicationConfig()
        timeScheduler = TestScheduler()
        gameAdvertising = GameAdvertisingImpl(
            mockApplicationConfigRepository,
            mockNetwork,
            serializerFactory,
            TestSchedulerFactory(timeScheduler)
        )
    }

    @Test
    fun `Advertise game immediately`() {
        // When
        val gameAdvertisingInfo = GameAdvertisingInfo(true, GameCommonId(UUID.fromString(gameCommonId)), "gameName", 8889)
        gameAdvertising.advertiseGame(gameAdvertisingInfo).test()
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)

        // Then
        verify { mockNetwork.sendAdvertisement(any(), expectedAdvertising) }
    }

    @Test
    fun `Advertise game every 2 seconds until stream is disposed`() {
        // When
        val gameAdvertisingInfo = GameAdvertisingInfo(true, GameCommonId(UUID.fromString(gameCommonId)), "gameName", 8889)
        val testObserver = gameAdvertising.advertiseGame(gameAdvertisingInfo).test()

        // Then
        timeScheduler.advanceTimeTo(2, TimeUnit.SECONDS)
        verify(exactly = 2) { mockNetwork.sendAdvertisement(any(), expectedAdvertising) }

        timeScheduler.advanceTimeTo(4, TimeUnit.SECONDS)
        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), expectedAdvertising) }

        testObserver.dispose()

        timeScheduler.advanceTimeTo(6, TimeUnit.SECONDS)
        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), any()) } // Still 3 calls
    }
}
