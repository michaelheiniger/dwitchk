package ch.qscqlmpa.dwitch.uitests.base

import android.content.res.Resources
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.R
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
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.matchesWithText
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank
import io.reactivex.Completable
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
        val client = ongoingGameComponent.websocketClientFactory.create() as TestWebsocketClient
        clientTestStub = WebsocketClientTestStub(client, serializerFactory)
    }

    private fun hookOngoingGameDependenciesCommon() {
        ongoingGameComponent = testRule.app.getGameComponent() as TestOngoingGameComponent
        serializerFactory = ongoingGameComponent.serializerFactory
    }

    protected fun initializeInitialGameSetup(cardsForPlayer: Map<Int, List<Card>>, rankForPlayer: Map<Int, Rank>) {
        (ongoingGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
            .setInstance(DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer))
    }

    protected fun dudeWaitASec(seconds: Long = 2L) {
        Completable.fromAction { Timber.i("Waiting for %d seconds...", seconds) }
                .delay(seconds, TimeUnit.SECONDS)
                .blockingGet()
    }


    protected fun setControlText(resourceId: Int, text: String) {
        onView(withId(resourceId)).perform(replaceText(text))
    }

    protected fun assertControlTextContent(resourceId: Int, textResourceId: Int) {
        onView(withId(resourceId)).check(matchesWithText(textResourceId))
    }

    protected fun assertControlIsDisplayed(resourceId: Int) {
        onView(withId(resourceId)).check(matches(isDisplayed()))
    }

    protected fun assertCurrentScreenIsHomeSreen() {
        assertControlIsDisplayed(R.id.gameListTv)
        assertControlTextContent(R.id.gameListTv, R.string.ma_game_list_tv)
    }
}
