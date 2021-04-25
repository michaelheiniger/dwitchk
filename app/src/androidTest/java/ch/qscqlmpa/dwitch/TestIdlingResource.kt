package ch.qscqlmpa.dwitch

import androidx.test.espresso.idling.CountingIdlingResource
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import org.tinylog.kotlin.Logger

class TestIdlingResource(resourceName: String) : DwitchIdlingResource {

    private val resource = CountingIdlingResource(resourceName)

    override fun isIdleNow(): Boolean {
        return resource.isIdleNow
    }

    override fun increment() {
        Logger.debug("Increment counter")
        resource.increment()
    }

    override fun decrement() {
        Logger.debug("Decrement counter")
        resource.decrement()
    }
}