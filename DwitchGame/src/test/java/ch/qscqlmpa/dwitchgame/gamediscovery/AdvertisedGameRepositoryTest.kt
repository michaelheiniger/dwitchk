package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommonutil.TestTimeProvider
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class AdvertisedGameRepositoryTest : BaseUnitTest() {
    private val mockGameDiscovery = mockk<GameDiscovery>(relaxed = true)
    private val mockIdlingResource = mockk<DwitchIdlingResource>(relaxed = true)
    private val timeProvider = TestTimeProvider()
    private lateinit var timeScheduler: TestScheduler
    private lateinit var repository: AdvertisedGameRepository

    private lateinit var gameDiscoveredSubject: PublishSubject<AdvertisedGame>
    private lateinit var resumableGamesSubject: PublishSubject<List<GameCommonId>>

    private val dtf = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss")

    private val advertisedNewGame1 = AdvertisedGame(
        isNew = true,
        gameName = "LOTR",
        gameCommonId = GameCommonId(1),
        gameIpAddress = "192.168.178.45",
        gamePort = 8889,
        LocalDateTime.parse("26.07.1989 10:00:00", dtf)
    )

    private val advertisedNewGame2 = AdvertisedGame(
        isNew = true,
        gameName = "SG-1",
        gameCommonId = GameCommonId(2),
        gameIpAddress = "192.168.178.67",
        gamePort = 8890,
        LocalDateTime.parse("26.07.1989 10:00:02", dtf)
    )

    private val advertisedExistingGame1 = AdvertisedGame(
        isNew = false,
        gameName = "GOT",
        gameCommonId = GameCommonId(3),
        gameIpAddress = "192.168.178.46",
        gamePort = 8888,
        LocalDateTime.parse("26.07.1989 10:00:00", dtf)
    )

    private val advertisedExistingGame2 = AdvertisedGame(
        isNew = false,
        gameName = "SG Atlantis",
        gameCommonId = GameCommonId(4),
        gameIpAddress = "192.168.178.68",
        gamePort = 8891,
        LocalDateTime.parse("26.07.1989 10:00:02", dtf)
    )

    @BeforeEach
    fun setup() {
        gameDiscoveredSubject = PublishSubject.create()
        every { mockGameDiscovery.listenForAdvertisedGames() } returns gameDiscoveredSubject
        resumableGamesSubject = PublishSubject.create()
        every { mockStore.observeGameCommonIdOfResumableGames() } returns resumableGamesSubject

        timeScheduler = TestScheduler()
        repository = AdvertisedGameRepository(
            mockStore,
            mockGameDiscovery,
            TestSchedulerFactory(timeScheduler),
            timeProvider,
            mockIdlingResource
        )
    }

    @Test
    fun `advertised games can be queried by their IP address`() {
        // Given
        repository.startListeningForAdvertisedGames()

        setCurrentTime(10, 0, 1)
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)

        assertThat(repository.getGame(advertisedNewGame1.gameIpAddress)).isNull()
        assertThat(repository.getGame(advertisedExistingGame1.gameIpAddress)).isNull()

        // When
        resumableGamesSubject.onNext(listOf(advertisedExistingGame1.gameCommonId))
        gameDiscoveredSubject.onNext(advertisedNewGame1)
        gameDiscoveredSubject.onNext(advertisedExistingGame1)

        // Then
        assertThat(repository.getGame(advertisedNewGame1.gameIpAddress)).isNotNull
        assertThat(repository.getGame(advertisedExistingGame1.gameIpAddress)).isNotNull
    }

    @Test
    fun `start listening for advertised games fills the repository with discovered _new_ games`() {
        // Given

        // When
        val testObserver = repository.observeAdvertisedGames().test()
        repository.startListeningForAdvertisedGames()
        setCurrentTime(10, 0, 1)
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
        resumableGamesSubject.onNext(emptyList())

        // Then
        testObserver.assertValueAt(0, emptyList())

        // When / Then
        gameDiscoveredSubject.onNext(advertisedNewGame1)
        testObserver.assertValueAt(1, listOf(advertisedNewGame1))

        // When / Then
        gameDiscoveredSubject.onNext(advertisedNewGame2)
        testObserver.assertValueAt(2, listOf(advertisedNewGame1, advertisedNewGame2))
    }

    @Test
    fun `start listening for advertised games fills the repository with discovered _existing_ games`() {
        // Given

        // When
        val testObserver = repository.observeAdvertisedGames().test()
        repository.startListeningForAdvertisedGames()
        setCurrentTime(10, 0, 1)
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
        resumableGamesSubject.onNext(listOf(advertisedExistingGame1.gameCommonId, advertisedExistingGame2.gameCommonId))

        // Then
        testObserver.assertValueAt(0, emptyList())

        // When / Then
        gameDiscoveredSubject.onNext(advertisedExistingGame1)
        testObserver.assertValueAt(1, listOf(advertisedExistingGame1))

        // When / Then
        gameDiscoveredSubject.onNext(advertisedExistingGame2)
        testObserver.assertValueAt(2, listOf(advertisedExistingGame1, advertisedExistingGame2))
    }

    @Test
    fun `stop listening for advertised games clears the repository`() {
        // Given
        val testObserver = repository.observeAdvertisedGames().test()
        repository.startListeningForAdvertisedGames()

        setCurrentTime(10, 0, 0)
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
        resumableGamesSubject.onNext(listOf(advertisedExistingGame1.gameCommonId))
        gameDiscoveredSubject.onNext(advertisedNewGame1)
        gameDiscoveredSubject.onNext(advertisedExistingGame1)

        testObserver.assertValueAt(0, emptyList())
        testObserver.assertValueAt(1, listOf(advertisedNewGame1))
        testObserver.assertValueAt(2, listOf(advertisedNewGame1, advertisedExistingGame1))
        assertThat(repository.getGame(advertisedNewGame1.gameIpAddress)).isEqualTo(advertisedNewGame1)
        assertThat(repository.getGame(advertisedExistingGame1.gameIpAddress)).isEqualTo(advertisedExistingGame1)

        // When stop listening (and time hasn't changed)
        repository.stopListeningForAdvertisedGames()

        // Then
        testObserver.assertValueAt(3, emptyList())
        testObserver.assertValueCount(4)
        assertThat(repository.getGame(advertisedNewGame1.gameIpAddress)).isNull()
        assertThat(repository.getGame(advertisedExistingGame1.gameIpAddress)).isNull()
    }

    @Test
    fun `repository is cleared of obsolete advertised games after a given time`() {
        // Given
        val testObserver = repository.observeAdvertisedGames().test()
        repository.startListeningForAdvertisedGames()

        setCurrentTime(10, 0, 1)
        timeScheduler.advanceTimeTo(0, TimeUnit.SECONDS)
        resumableGamesSubject.onNext(emptyList())
        gameDiscoveredSubject.onNext(advertisedNewGame1)

        testObserver.assertValueAt(0, emptyList())
        testObserver.assertValueAt(1, listOf(advertisedNewGame1))

        // When advertisements are too old
        setCurrentTime(10, 0, 2 + AdvertisedGameRepository.GAME_AD_TIMEOUT_SEC)
        timeScheduler.advanceTimeBy(AdvertisedGameRepository.GAME_AD_TIMEOUT_SEC.toLong() + 1L, TimeUnit.SECONDS)

        // Then they are removed
        testObserver.assertValueAt(2, emptyList())
        testObserver.assertValueCount(3)
    }

    private fun setCurrentTime(hours: Int, minutes: Int, seconds: Int) {
        timeProvider.nowProvider = { LocalDateTime.parse("26.07.1989 $hours:$minutes:$seconds", dtf) }
    }
}
