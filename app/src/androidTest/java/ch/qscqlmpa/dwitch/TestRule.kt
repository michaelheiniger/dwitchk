package ch.qscqlmpa.dwitch

import android.app.Activity
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.rule.ActivityTestRule
import ch.qscqlmpa.dwitch.app.TestApp

class TestRule<T : Activity>(activityClass: Class<T>) : ActivityTestRule<T>(activityClass, true, false) {

    lateinit var app: TestApp

    fun init() {
        app = getApplicationContext()
    }
}
