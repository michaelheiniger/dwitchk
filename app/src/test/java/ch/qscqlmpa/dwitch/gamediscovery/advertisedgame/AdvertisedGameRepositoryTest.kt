package ch.qscqlmpa.dwitch.gamediscovery.advertisedgame


import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.gamediscovery.StubGameDiscovery
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitch.gamediscovery.GameDiscovery
import io.mockk.*
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalTime
import org.junit.After
import org.junit.Before
import org.junit.Test

class AdvertisedGameRepositoryTest : BaseUnitTest() {

    private val mockGameDiscovery = mockk<GameDiscovery>()

    private lateinit var stubGameDiscovery: StubGameDiscovery

    private lateinit var repository: AdvertisedGameRepository

    @Before
    override fun setup() {
        super.setup()
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockGameDiscovery)
    }

    @Test
    fun observeAdvertisedGames_shouldEmitListOfGames() {

        stubGameDiscovery = StubGameDiscovery()
        repository = AdvertisedGameRepository(stubGameDiscovery)

        val game1 = AdvertisedGame("Kaamelott", "192.168.1.1", 8890, LocalTime.now())
        val game2 = AdvertisedGame("Dwiiiitch !", "192.168.1.2", 8891, LocalTime.now())

        val testObserver = repository.listenForAdvertisedGames()
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .test()

        testObserver.assertValues(emptyList())

        stubGameDiscovery.emitGame(game1)

        testObserver.assertValues(emptyList(), listOf(game1))

        stubGameDiscovery.emitGame(game2)

        testObserver.assertValues(emptyList(), listOf(game1), listOf(game1, game2))

        confirmVerified(mockGameDiscovery)
    }

    @Test
    fun observeAdvertisedGames_shouldStopListening() {

        every { mockGameDiscovery.stopListening() } just Runs

        repository = AdvertisedGameRepository(mockGameDiscovery)

        repository.stopListening()
        verify { mockGameDiscovery.stopListening() }

        confirmVerified(mockGameDiscovery)
    }
}