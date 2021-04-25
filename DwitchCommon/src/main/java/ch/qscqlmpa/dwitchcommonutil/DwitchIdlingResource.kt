package ch.qscqlmpa.dwitchcommonutil

/**
 * Counter representing the number of async ongoing operations.
 * Call [increment] to increment the counter: Compose UI checks are paused.
 * Call [decrement] to decrease the counter: if [decrement] has been called as many times as [increment],
 * Compose UI checks are resumed.
 * Notes: - [decrement] must always be called after a matching increment.
 *        - [decrement] must eventually be called otherwise the test is going to fail (timeout).
 */
interface DwitchIdlingResource {
    fun isIdleNow(): Boolean
    fun increment()
    fun decrement()
}
