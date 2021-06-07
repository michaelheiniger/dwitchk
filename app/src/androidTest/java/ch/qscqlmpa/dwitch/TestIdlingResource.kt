package ch.qscqlmpa.dwitch

import androidx.test.espresso.idling.CountingIdlingResource
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import org.tinylog.kotlin.Logger

class TestIdlingResource(resourceName: String) : DwitchIdlingResource {

    private val resource = CountingIdlingResource(resourceName)

    override fun isIdleNow(): Boolean {
        return resource.isIdleNow
    }

    override fun increment(reason: String) {
        Logger.debug("Increment counter: $reason")
        resource.increment()
    }

    override fun decrement(reason: String) {
        Logger.debug("Decrement counter: $reason")
        resource.decrement()
    }
}
