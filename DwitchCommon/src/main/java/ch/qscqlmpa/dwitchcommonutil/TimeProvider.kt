package ch.qscqlmpa.dwitchcommonutil

import org.joda.time.LocalDateTime

interface TimeProvider {
    fun now(): LocalDateTime
}

class RealTimeProvider : TimeProvider {
    override fun now(): LocalDateTime {
        return LocalDateTime.now()
    }
}

class TestTimeProvider : TimeProvider {

    lateinit var nowProvider: () -> LocalDateTime

    override fun now(): LocalDateTime {
        return nowProvider()
    }
}
