package ch.qscqlmpa.dwitch.uitests.utils

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not

object UiUtil {

    private val res = InstrumentationRegistry.getInstrumentation().targetContext.resources

    fun matchesWithText(resource: Int): ViewAssertion {
        return matches(withText(res.getString(resource)))
    }

    fun matchesWithErrorText(resource: Int): ViewAssertion {
        return matches(hasErrorText(res.getString(resource)))
    }

    fun clickOnButton(resourceId: Int) {
        onView(withId(resourceId)).perform(ViewActions.click())
    }

    fun clickOnRecyclerViewElement(recyclerViewId: Int, elementId: Int, elementPosition: Int) {
        onView(withRecyclerView(recyclerViewId).atPositionOnView(elementPosition, elementId))
            .perform(ViewActions.click())
    }

    fun assertRecyclerViewElementText(recyclerViewId: Int, elementId: Int, elementPosition: Int, text: String) {
        onView(withRecyclerView(recyclerViewId)
            .atPositionOnView(elementPosition, elementId))
            .check(matches(withText(text)))
    }

    fun assertRecyclerViewElementText(recyclerViewId: Int, elementId: Int, elementPosition: Int, stringMatcher: Matcher<String>) {
        onView(withRecyclerView(recyclerViewId)
            .atPositionOnView(elementPosition, elementId))
            .check(matches(withText(stringMatcher)))
    }

    fun assertControlEnabled(resourceId: Int, enabled: Boolean) {
        if (enabled) {
            onView(withId(resourceId)).check(matches(isEnabled()))
        } else {
            onView(withId(resourceId)).check(matches(not(isEnabled())))
        }
    }

    fun elementIsDisplayed(resourceId: Int) {
        onView(withId(resourceId)).check(matches(isDisplayed()))
    }

    fun assertControlTextContent(resourceId: Int, textResourceId: Int) {
        onView(withId(resourceId)).check(matchesWithText(textResourceId))
    }
}