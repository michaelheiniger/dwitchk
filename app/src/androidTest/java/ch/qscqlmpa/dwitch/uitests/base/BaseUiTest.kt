package ch.qscqlmpa.dwitch.uitests.base

import android.content.res.Resources
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.TestRule
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitchcommunication.di.TestCommunicationComponent
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.gamediscovery.TestNetworkAdapter
import ch.qscqlmpa.dwitchgame.ongoinggame.di.TestOngoingGameComponent
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
abstract class BaseUiTest {

    @get:Rule
    var testRule = TestRule(MainActivity::class.java)

    protected val gameName = "LOTR"

    protected val hostName = "Aragorn"

    protected lateinit var res: Resources

    private lateinit var storeComponent: TestStoreComponent
    private lateinit var gameComponent: TestGameComponent

    private lateinit var ongoingGameComponent: TestOngoingGameComponent
    private lateinit var communicationComponent: TestCommunicationComponent

    protected lateinit var networkAdapter: TestNetworkAdapter

    protected lateinit var store: Store
    protected lateinit var inGameStore: InGameStore

    protected lateinit var commSerializerFactory: SerializerFactory

    protected lateinit var serverTestStub: ServerTestStub
    protected lateinit var clientTestStub: ClientTestStub

    @Before
    fun setup() {
        res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        testRule.init()
    }

    protected fun launch() {
        testRule.launchActivity(null)

        gameComponent = testRule.app.testGameComponent
        storeComponent = testRule.app.testStoreComponent as TestStoreComponent

        store = storeComponent.store
        networkAdapter = gameComponent.networkListener as TestNetworkAdapter
    }

    protected fun hookOngoingGameDependenciesForHost() {
        hookOngoingGameDependenciesCommon()
        commSerializerFactory = communicationComponent.serializerFactory
        serverTestStub = communicationComponent.serverTestStub
    }

    protected fun hookOngoingGameDependenciesForGuest() {
        hookOngoingGameDependenciesCommon()
        commSerializerFactory = communicationComponent.serializerFactory
        clientTestStub = communicationComponent.clientTestStub
    }

    private fun hookOngoingGameDependenciesCommon() {
        ongoingGameComponent = testRule.app.ongoingGameComponent as TestOngoingGameComponent
        inGameStore = testRule.app.inGameStoreComponent!!.inGameStore
        communicationComponent = testRule.app.communicationComponent as TestCommunicationComponent
    }

    protected fun initializeInitialGameSetup(cardsForPlayer: Map<Int, List<Card>>, rankForPlayer: Map<Int, Rank>) {
        (ongoingGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
            .setInstance(DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer))
    }

    protected fun initializeNewRoundCardDealer(cardsForPlayer: Map<Int, List<Card>>) {
        (ongoingGameComponent.cardDealerFactory as DeterministicCardDealerFactory)
            .setInstance(DeterministicCardDealer(cardsForPlayer))
    }

    protected fun dudeWaitASec(seconds: Long = 2L) {
        Completable.fromAction { logger.info { "Waiting for $seconds seconds..." } }
            .delay(seconds, TimeUnit.SECONDS)
            .blockingSubscribe()
    }

    protected fun dudeWaitAMillisSec(ms: Long = 500L) {
        Completable.fromAction { logger.info { "Waiting for $ms milliseconds..." } }
            .delay(ms, TimeUnit.MILLISECONDS)
            .blockingSubscribe()
    }

    protected fun assertCurrentScreenIsHomeSreen() {
        UiUtil.elementIsDisplayed(R.id.gameListTv)
        UiUtil.assertControlTextContent(R.id.gameListTv, R.string.ma_game_list_tv)
    }

    protected fun buildSerializedAdvertisedGame(
        isNew: Boolean,
        gameName: String,
        gameCommonId: GameCommonId,
        gamePort: Int
    ): String {
        return "{\"isNew\": $isNew, \"gameCommonId\":{\"value\":${gameCommonId.value}},\"gameName\":\"$gameName\",\"gamePort\":$gamePort}"
    }

    companion object : KLogging()
}
