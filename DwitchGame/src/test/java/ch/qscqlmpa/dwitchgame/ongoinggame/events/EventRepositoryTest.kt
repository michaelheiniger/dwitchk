package ch.qscqlmpa.dwitchgame.ongoinggame.events

import ch.qscqlmpa.dwitchgame.ongoinggame.common.EventRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventRepositoryTest {

    private lateinit var eventRepository: MyEventRepository

    @BeforeEach
    fun setup() {
        eventRepository = MyEventRepository()
    }

    @Test
    fun `No event emitted because no observer`() {
        val event = "salut"
        assertThat(eventRepository.consumeLastEvent()).isNull()

        // No observer exists

        eventRepository.notify(event)

        assertThat(eventRepository.consumeLastEvent()).isEqualTo(event)

        // How to check that no event is emitted since one cannot subscribe to it ?
    }

    @Test
    fun `Event emitted because existing observers`() {
        val event = "salut"
        assertThat(eventRepository.consumeLastEvent()).isNull()

        // An observer exists
        val testObserver = eventRepository.observeEvents().test()

        eventRepository.notify(event)

        testObserver.assertValue(event)

        // Event must be cleared so that it can be consumed only once
        assertThat(eventRepository.consumeLastEvent()).isNull()
    }
}

private class MyEventRepository : EventRepository<String>()
