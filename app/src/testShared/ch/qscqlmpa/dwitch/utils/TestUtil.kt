package ch.qscqlmpa.dwitch.utils

import dagger.Lazy
import org.mockito.ArgumentCaptor
import org.mockito.Mockito

class TestUtil {

    companion object {

        fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

        /**
         * Prevents "java.lang.IllegalStateException: ArgumentMatchers.any(MyClass::class.java) must not be null"
         */
        fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

        fun <T> any(): T = Mockito.any()

        fun <T> eq(value: T): T = Mockito.eq(value)

        fun <T> lazyOf(instance: T): Lazy<T> {
            return LazyImpl(instance)
        }
    }
}