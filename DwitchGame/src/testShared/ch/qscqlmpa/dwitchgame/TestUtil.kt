package ch.qscqlmpa.dwitchgame

import dagger.Lazy

class TestUtil {

    companion object {

        fun <T> lazyOf(instance: T): Lazy<T> {
            return LazyImpl(instance)
        }
    }
}