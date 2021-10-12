package ch.qscqlmpa.dwitchcommunication.gameadvertising

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchcommunication.*
import ch.qscqlmpa.dwitchcommunication.common.ApplicationConfigRepository
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import com.jakewharton.rxrelay3.BehaviorRelay
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit

class GameAdvertiserTest : BaseUnitTest() {

    private val mockApplicationConfigRepository = mockk<ApplicationConfigRepository>(relaxed = true)
    private val mockWLanConnectionRepository = mockk<WLanConnectionRepository>(relaxed = true)
    private val mockNetwork = mockk<Network>(relaxed = true)
    private lateinit var timeScheduler: TestScheduler

    private lateinit var gameAdvertiser: GameAdvertiser

    private lateinit var wlanConnectionRepositoryRelay: BehaviorRelay<DeviceConnectionState>

    private val gameCommonId = "a06ef013-5788-4fd4-adad-aa90a2da8c7c"
    private val gameInfo = GameInfo(
        isNew = true,
        gameName = "LOTR",
        gameCommonId = GameCommonId(UUID.fromString(gameCommonId))
    )

    @BeforeEach
    fun setup() {
        every { mockApplicationConfigRepository.config } returns testApplicationConfig()
        wlanConnectionRepositoryRelay = BehaviorRelay.create()
        every { mockWLanConnectionRepository.observeConnectionState() } returns wlanConnectionRepositoryRelay
        timeScheduler = TestScheduler()
        gameAdvertiser = GameAdvertiserImpl(
            mockApplicationConfigRepository,
            mockWLanConnectionRepository,
            mockNetwork,
            serializerFactory,
            TestSchedulerFactory(timeScheduler)
        )
    }

    @Test
    fun `Advertise game immediately`() {
        // Given
        wlanConnectionRepositoryRelay.accept(DeviceConnectionState.OnWifi("192.168.1.2"))

        // When
        gameAdvertiser.advertiseGame(gameInfo).test()
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)

        // Then
        val serializedGameAdInfo = CapturingSlot<String>()
        verify { mockNetwork.sendAdvertisement(any(), capture(serializedGameAdInfo)) }
        val gameAdInfo = serializerFactory.unserializeGameInfo(serializedGameAdInfo.captured)
        assertThat(gameAdInfo).usingRecursiveComparison().ignoringFields("discoveryTime")
            .isEqualTo(GameAdvertisingInfo(gameInfo = gameInfo, "192.168.1.2"))
    }

    @Test
    fun `Advertise game every 2 seconds`() {
        // Given
        wlanConnectionRepositoryRelay.accept(DeviceConnectionState.OnWifi("192.168.1.2"))

        // When
        gameAdvertiser.advertiseGame(gameInfo).test()

        // Then
        timeScheduler.advanceTimeTo(2, TimeUnit.SECONDS)
        verify(exactly = 2) { mockNetwork.sendAdvertisement(any(), any()) }

        timeScheduler.advanceTimeTo(4, TimeUnit.SECONDS)
        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), any()) }
    }

    @Test
    fun `Advertise game until stream is disposed`() {
        // Given
        wlanConnectionRepositoryRelay.accept(DeviceConnectionState.OnWifi("192.168.1.2"))
        val testObserver = gameAdvertiser.advertiseGame(gameInfo).test()
        timeScheduler.advanceTimeTo(4, TimeUnit.SECONDS)
        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), any()) }

        // When dispose
        testObserver.dispose()

        // Then no more ad is sent
        timeScheduler.advanceTimeTo(6, TimeUnit.SECONDS)
        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), any()) } // Still 3 calls
    }
}
