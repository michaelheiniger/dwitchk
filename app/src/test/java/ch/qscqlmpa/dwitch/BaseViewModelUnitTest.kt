package ch.qscqlmpa.dwitch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule

abstract class BaseViewModelUnitTest : BaseUnitTest() {

    private val lifecycleOwner = mockk<LifecycleOwner>()
    private val lifecycle = LifecycleRegistry(lifecycleOwner)

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    override fun setup() {
        super.setup()
        every { lifecycleOwner.lifecycle } returns lifecycle
    }

    protected fun subscribeToPublishers(vararg liveDatas: LiveData<out Any>) {
        liveDatas.forEach { liveData -> liveData.observe(lifecycleOwner, { /* Nothing to do*/ }) }
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}