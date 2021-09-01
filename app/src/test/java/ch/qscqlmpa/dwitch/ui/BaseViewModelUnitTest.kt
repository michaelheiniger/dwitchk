package ch.qscqlmpa.dwitch.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE) // Prevent missing AndroidManifest log
@RunWith(RobolectricTestRunner::class) // Needed because of logging
abstract class BaseViewModelUnitTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @After
    fun clearMocks() {
        clearAllMocks()
    }
}
