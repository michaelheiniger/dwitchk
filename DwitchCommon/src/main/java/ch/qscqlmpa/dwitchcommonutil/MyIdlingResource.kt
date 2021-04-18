package ch.qscqlmpa.dwitchcommonutil

interface MyIdlingResource {
    fun isIdleNow(): Boolean
    fun increment()
    fun decrement()
}