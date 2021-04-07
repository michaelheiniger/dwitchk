package ch.qscqlmpa.dwitchgame.api

import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingImpl
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.gameadvertising.network.Network
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostFacadeImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class GameAdvertisingTest : BaseUnitTest() {

    private val mockCommunicationStateRepository = mockk<HostCommunicationStateRepository>(relaxed = true)
    private val mockNetwork = mockk<Network>(relaxed = true)
    private val mockHostCommunicator = mockk<HostCommunicator>(relaxed = true)

    private lateinit var hostFacade: HostFacade

    private lateinit var timeScheduler: TestScheduler

    @BeforeEach
    override fun setup() {
        val testSchedulerFactory = TestSchedulerFactory()
        timeScheduler = TestScheduler()
        testSchedulerFactory.setTimeScheduler(timeScheduler)
        val gameAdvertising = GameAdvertisingImpl(serializerFactory, testSchedulerFactory, mockNetwork)
        hostFacade = HostFacadeImpl(mockCommunicationStateRepository, mockHostCommunicator, gameAdvertising)
    }

    @Test
    fun `Advertise game immediately`() {
        val gameAdvertisingInfo = GameAdvertisingInfo(true, GameCommonId(1), "gameName", 8889)

        hostFacade.advertiseGame(gameAdvertisingInfo).test()

        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)

        verify { mockNetwork.sendAdvertisement(any(), "{\"isNew\":true,\"gameCommonId\":{\"value\":1},\"gameName\":\"gameName\",\"gamePort\":8889}") }
    }

    @Test
    fun `Advertise game every 2 seconds until stream is disposed`() {
        val gameAdvertisingInfo = GameAdvertisingInfo(true, GameCommonId(1), "gameName", 8889)
        val expectedAdvertising = "{\"isNew\":true,\"gameCommonId\":{\"value\":1},\"gameName\":\"gameName\",\"gamePort\":8889}"

        val testObserver = hostFacade.advertiseGame(gameAdvertisingInfo).test()

        timeScheduler.advanceTimeTo(2, TimeUnit.SECONDS)

        verify(exactly = 2) { mockNetwork.sendAdvertisement(any(), expectedAdvertising) }

        timeScheduler.advanceTimeTo(4, TimeUnit.SECONDS)

        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), expectedAdvertising) }

        testObserver.dispose()

        timeScheduler.advanceTimeTo(6, TimeUnit.SECONDS)

        verify(exactly = 3) { mockNetwork.sendAdvertisement(any(), any()) } // Still 3 calls
    }
}
