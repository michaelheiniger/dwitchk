package ch.qscqlmpa.dwitchgame

import dagger.Lazy

class LazyImpl<T> constructor(private val instance: T) : Lazy<T> {

    override fun get(): T {
        return instance
    }
}
