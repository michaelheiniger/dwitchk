package ch.qscqlmpa.dwitch.uitests

import android.content.res.Resources
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.TestRule
import ch.qscqlmpa.dwitch.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitch.ongoinggame.TestOngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.TestWebsocketClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.WebsocketClientTestStub
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.TestWebsocketServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.WebsocketServerTestStub
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.persistence.AppRoomDatabase
import ch.qscqlmpa.dwitch.persistence.GameDao
import ch.qscqlmpa.dwitch.persistence.PlayerDao
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup //FIXME cannot be used in app/androidTest since it is in DwitchEngine/test
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank
import io.reactivex.Completable
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
abstract class BaseUiTest {

    @get:Rule
    var testRule = TestRule(MainActivity::class.java)

    protected lateinit var res: Resources

    private lateinit var database: AppRoomDatabase
    protected lateinit var playerDao: PlayerDao
    protected lateinit var gameDao: GameDao
    protected lateinit var inGameStore: InGameStore

    private lateinit var ongoingGameComponent: TestOngoingGameComponent
    protected lateinit var serializerFactory: SerializerFactory
    protected lateinit var networkAdapter: TestNetworkAdapter

    protected lateinit var serverTestStub: WebsocketServerTestStub
    protected lateinit var clientTestStub: WebsocketClientTestStub

    open fun setup() {
        res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        testRule.init()
    }

    protected fun launch() {
        testRule.launchActivity(null)

        database = testRule.app.testAppComponent.database
        gameDao = database.gameDao()
        playerDao = database.playerDao()
        networkAdapter = testRule.app.testAppComponent.testNetworkListener
    }

    protected fun hookOngoingGameDependenciesForHost() {
        hookOngoingGameDependenciesCommon()
        val server = ongoingGameComponent.websocketServer as TestWebsocketServer
        serverTestStub = WebsocketServerTestStub(server, serializerFactory)
        inGameStore = ongoingGameComponent.inGameStore
    }

    protected fun hookOngoingGameDependenciesForGuest() {
        hookOngoingGameDependenciesCommon()
        val client = ongoingGameComponent.websocketClient as TestWebsocketClient
        clientTestStub = WebsocketClientTestStub(client, serializerFactory)
    }

    private fun hookOngoingGameDependenciesCommon() {
        ongoingGameComponent = testRule.app.getGameComponent() as TestOngoingGameComponent
        serializerFactory = ongoingGameComponent.serializerFactory
    }

    protected fun initializeInitialGameSetup(cardsForPlayer: Map<Int, List<Card>>, rankForPlayer: Map<Int, Rank>) {
        val initialGameSetup = ongoingGameComponent.initialGameSetupFactory.getInitialGameSetup(cardsForPlayer.size) as DeterministicInitialGameSetup
        initialGameSetup.initialize(cardsForPlayer, rankForPlayer)
    }

    protected fun dudeWaitAMinute(seconds: Long = 3L) {
        Completable.fromAction { Timber.i("Waiting for %d seconds...", seconds) }
                .delay(seconds, TimeUnit.SECONDS)
                .blockingGet()
    }

    protected fun matchesWithText(resource: Int): ViewAssertion {
        return matches(withText(res.getString(resource)))
    }

    protected fun matchesWithErrorText(resource: Int): ViewAssertion {
        return matches(hasErrorText(res.getString(resource)))
    }

    protected fun clickOnButton(resourceId: Int) {
        Espresso.onView(ViewMatchers.withId(resourceId)).perform(ViewActions.click())
    }

    protected fun assertControlEnabled(resourceId: Int, enabled: Boolean) {
        if (enabled) {
            Espresso.onView(ViewMatchers.withId(resourceId)).check(matches(ViewMatchers.isEnabled()))
        } else {
            Espresso.onView(ViewMatchers.withId(resourceId)).check(matches(Matchers.not(ViewMatchers.isEnabled())))
        }
    }

    protected fun setControlText(resourceId: Int, text: String) {
        Espresso.onView(ViewMatchers.withId(resourceId)).perform(ViewActions.replaceText(text))
    }

    protected fun assertControlTextContent(resourceId: Int, textResourceId: Int) {
        Espresso.onView(ViewMatchers.withId(resourceId)).check(matchesWithText(textResourceId))
    }

    protected fun assertControlIsDisplayed(resourceId: Int) {
        Espresso.onView(ViewMatchers.withId(resourceId)).check(matches(ViewMatchers.isDisplayed()))
    }

}
