package ch.qscqlmpa.dwitch.uitests.base

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.qscqlmpa.dwitch.TestRule
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseUiTest2 {

    @get:Rule
    var testRule = TestRule(MainActivity::class.java)


    @Before
    open fun setup() {
        testRule.init()
    }

    protected fun launch() {
        testRule.launchActivity(null)

//        val testAppComponent = testRule.app.testAppComponent
//        val testGameComponent = testRule.app.testGameComponent
//        val testStoreComponent = testRule.app.testStoreComponent
//
//        store = testStoreComponent.store
//
//        networkAdapter = testGameComponent.networkListener as TestNetworkAdapter
    }
}
