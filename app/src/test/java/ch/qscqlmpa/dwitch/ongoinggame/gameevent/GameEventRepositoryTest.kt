package ch.qscqlmpa.dwitch.ongoinggame.gameevent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GameEventRepositoryTest {

    private lateinit var gameEventRepository: GameEventRepository

    @Before
    fun setup() {
        gameEventRepository = GameEventRepository()
    }

    @Test
    fun `No event emitted because no observer`() {

        assertNull(gameEventRepository.getLastEvent())

        // No observer exists

        gameEventRepository.notifyOfEvent(GameEvent.GameCanceled)

        assertEquals(GameEvent.GameCanceled, gameEventRepository.getLastEvent())

        // How to check that no event is emitted since one cannot subscribe to it ?
    }

    @Test
    fun `Event emitted because existing observers`() {

        assertNull(gameEventRepository.getLastEvent())

        // An observer exists
        val testObserver = gameEventRepository.observeEvents().test()

        gameEventRepository.notifyOfEvent(GameEvent.GameCanceled)

        testObserver.assertValue(GameEvent.GameCanceled)

        // Event must be cleared so that it can be consumed only once
        assertNull(gameEventRepository.getLastEvent())
    }
}