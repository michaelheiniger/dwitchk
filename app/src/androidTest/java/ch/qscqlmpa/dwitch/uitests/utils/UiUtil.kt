package ch.qscqlmpa.dwitch.uitests.utils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher

object UiUtil {

    private val res = InstrumentationRegistry.getInstrumentation().targetContext.resources

    fun setControlText(resourceId: Int, text: String) {
        onView(withId(resourceId)).perform(ViewActions.replaceText(text))
    }

    fun matchesWithText(resource: Int): ViewAssertion {
        return matches(withText(res.getString(resource)))
    }

    fun matchesWithText(text: String): ViewAssertion {
        return matches(withText(text))
    }

    fun matchesWithErrorText(resource: Int): ViewAssertion {
        return matches(hasErrorText(res.getString(resource)))
    }

    fun clickOnButton(resourceId: Int) {
        onView(withId(resourceId)).perform(click())
    }

    fun clickOnRecyclerViewElement(recyclerViewId: Int, elementId: Int, elementPosition: Int) {
        onView(withRecyclerView(recyclerViewId).atPositionOnView(elementPosition, elementId))
            .perform(click())
    }

    fun assertRecyclerViewElementText(recyclerViewId: Int, elementId: Int, elementPosition: Int, text: String) {
        onView(withRecyclerView(recyclerViewId).atPositionOnView(elementPosition, elementId))
            .check(matches(withText(text)))
    }

    fun assertRecyclerViewElementText(recyclerViewId: Int, elementId: Int, elementPosition: Int, resource: Int) {
        onView(withRecyclerView(recyclerViewId).atPositionOnView(elementPosition, elementId))
            .check(matches(withText(res.getString(resource))))
    }

    fun assertRecyclerViewElementText(recyclerViewId: Int, elementId: Int, elementPosition: Int, stringMatcher: Matcher<String>) {
        onView(withRecyclerView(recyclerViewId).atPositionOnView(elementPosition, elementId))
            .check(matches(withText(stringMatcher)))
    }

    fun assertControlEnabled(resourceId: Int, enabled: Boolean) {
        if (enabled) {
            onView(withId(resourceId)).check(matches(isEnabled()))
        } else {
            onView(withId(resourceId)).check(matches(not(isEnabled())))
        }
    }

    fun assertCheckboxChecked(resourceId: Int, checked: Boolean) {
        if (checked) {
            onView(withId(resourceId)).check(matches(isChecked()))
        } else {
            onView(withId(resourceId)).check(matches(not(isChecked())))
        }
    }

    fun elementIsDisplayed(resourceId: Int) {
        onView(withId(resourceId)).check(matches(isDisplayed()))
    }

    fun assertControlTextContent(resourceId: Int, textResourceId: Int) {
        onView(withId(resourceId)).check(matchesWithText(textResourceId))
    }

    fun assertControlTextContent(resourceId: Int, text: String) {
        onView(withId(resourceId)).check(matchesWithText(text))
    }

    fun assertControlTextContent(resourceId: Int, matcher: Matcher<String>) {
        onView(withId(resourceId)).check(matches(withText(matcher)))
    }

    fun assertControlErrorTextContent(resourceId: Int, textResourceId: Int) {
        onView(withId(resourceId)).check(matchesWithErrorText(textResourceId))
    }

    fun assertTextInputLayoutHint(resourceId: Int, textResourceId: Int) {
        onView(withId(resourceId)).check(matches(hasTextInputLayoutHintText(res.getString(textResourceId))))
    }

    private fun hasTextInputLayoutHintText(expectedErrorText: String): Matcher<View> = object : TypeSafeMatcher<View>() {

        override fun describeTo(description: Description?) {}

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val error = item.hint ?: return false
            val hint = error.toString()
            return expectedErrorText == hint
        }
    }
}
