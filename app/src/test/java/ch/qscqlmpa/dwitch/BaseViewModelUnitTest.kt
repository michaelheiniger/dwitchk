package ch.qscqlmpa.dwitch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseViewModelUnitTest {

    private val lifecycleOwner = mockk<LifecycleOwner>()
    private val lifecycle = LifecycleRegistry(lifecycleOwner)

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        every { lifecycleOwner.lifecycle } returns lifecycle
    }

    @After
    fun clearMocks() {
        clearAllMocks()
    }

    protected fun subscribeToPublishers(vararg liveDatas: LiveData<out Any>) {
        liveDatas.forEach { liveData -> liveData.observe(lifecycleOwner, { /* Nothing to do*/ }) }
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}