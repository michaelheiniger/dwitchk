package ch.qscqlmpa.dwitch.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Rule

abstract class BaseViewModelUnitTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @After
    fun clearMocks() {
        clearAllMocks()
    }
}
