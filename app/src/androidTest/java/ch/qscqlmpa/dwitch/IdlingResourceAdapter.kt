package ch.qscqlmpa.dwitch

import androidx.test.espresso.idling.CountingIdlingResource
import ch.qscqlmpa.dwitchcommonutil.MyIdlingResource
import org.tinylog.kotlin.Logger

class IdlingResourceAdapter : MyIdlingResource {

    private val resource = CountingIdlingResource("resourceName") //TODO: resourceName ???

    override fun isIdleNow(): Boolean {
        return resource.isIdleNow
    }

    override fun increment() {
        resource.increment()
        Logger.debug("Increment counter")
    }

    override fun decrement() {
        resource.decrement()
        Logger.debug("Decrement counter")
    }
}