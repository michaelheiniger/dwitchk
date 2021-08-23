package ch.qscqlmpa.dwitchgame.home.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.InsertGameResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.subjects.PublishSubject
import org.joda.time.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class JoinNewGameUsecaseTest : BaseUnitTest() {

    private val mockGameLifecycleEventRepository = mockk<GuestGameLifecycleEventRepository>(relaxed = true)

    private lateinit var newGameUsecase: JoinNewGameUsecase

    private val gameLocalId = 1L
    private val gameCommonId = GameCommonId(123L)
    private val gameName = "Kaamelott"
    private val gamePort = 8889
    private val localPlayerLocalId = 10L
    private val gameIpAddress = "192.168.1.1"
    private val advertisedGame = AdvertisedGame(true, gameName, gameCommonId, gameIpAddress, gamePort, LocalDateTime.now())
    private val playerName = "Lancelot"

    private lateinit var gameLifecycleEventSubject: PublishSubject<GuestGameLifecycleEvent>

    @BeforeEach
    fun setup() {
        gameLifecycleEventSubject = PublishSubject.create()
        every { mockGameLifecycleEventRepository.observeEvents() } returns gameLifecycleEventSubject

        newGameUsecase = JoinNewGameUsecase(mockGameLifecycleEventRepository, mockStore)
        every { mockStore.insertGameForGuest(any(), any(), any()) } returns
            InsertGameResult(gameLocalId, gameCommonId, gameName, localPlayerLocalId)
    }

    @Test
    fun `should wait for join ACK from Host`() {
        // Given

        // When
        val testObserver = newGameUsecase.joinGame(advertisedGame, playerName).test()
        gameLifecycleEventSubject.onNext(GuestGameLifecycleEvent.GameJoined)

        // Then
        testObserver.assertComplete()
    }

    @Test
    fun `should wait for rejoin ACK from Host`() {
        // Given

        // When
        val testObserver = newGameUsecase.joinGame(advertisedGame, playerName).test()
        gameLifecycleEventSubject.onNext(GuestGameLifecycleEvent.GameRejoined)

        // Then
        testObserver.assertComplete()
    }

    @Test
    fun `should create game in store`() {
        // Given

        // When
        newGameUsecase.joinGame(advertisedGame, playerName).test()
        gameLifecycleEventSubject.onNext(GuestGameLifecycleEvent.GameJoined)

        // Then
        verify { mockStore.insertGameForGuest(gameName, gameCommonId, playerName) }
    }

    @Test
    fun `should emit _game created_ game lifecycle event`() {
        // Given

        // When
        newGameUsecase.joinGame(advertisedGame, playerName).test()
        gameLifecycleEventSubject.onNext(GuestGameLifecycleEvent.GameJoined)

        // Then
        verify {
            mockGameLifecycleEventRepository.notify(
                GuestGameLifecycleEvent.GameSetUp(GameJoinedInfo(gameLocalId, localPlayerLocalId, gameIpAddress, gamePort))
            )
        }
    }
}
