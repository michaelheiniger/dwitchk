package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource

class ProdIdlingResource : DwitchIdlingResource {
    override fun isIdleNow(): Boolean {
        return true
    }

    override fun increment(reason: String) {
        // Nothing to do: this is the production implementation
    }

    override fun decrement(reason: String) {
        // Nothing to do: this is the production implementation
    }
}
