package ch.qscqlmpa.dwitch.base

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule

abstract class BaseUiUnitTest {

    private val res = InstrumentationRegistry.getInstrumentation().targetContext.resources

    @get:Rule
    val composeTestRule = createComposeRule() // Must be public

    protected fun getString(resourceId: Int): String {
        return res.getString(resourceId)
    }

    protected fun launchTestWithContent(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            MaterialTheme {
                content()
            }
        }
    }
}
