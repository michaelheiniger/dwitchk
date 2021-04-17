package ch.qscqlmpa.dwitch.uitests.base

import android.content.res.Resources
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.TestApp
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitchcommunication.di.TestCommunicationComponent
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.gamediscovery.network.TestNetworkAdapter
import ch.qscqlmpa.dwitchgame.ongoinggame.di.TestOngoingGameComponent
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
abstract class BaseUiTest {

    @get:Rule
    val testRule = createAndroidComposeRule<MainActivity>()

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

    lateinit var app: TestApp

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        launch()
    }

    protected fun launch() { //TODO: Rename when all tests have been adapted and set to private
        gameComponent = app.testGameComponent
        storeComponent = app.testStoreComponent

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
        ongoingGameComponent = app.ongoingGameComponent as TestOngoingGameComponent
        inGameStore = app.inGameStoreComponent!!.inGameStore
        communicationComponent = app.communicationComponent as TestCommunicationComponent
    }

    protected fun initializeInitialGameSetup(
        cardsForPlayer: Map<DwitchPlayerId, Set<Card>>,
        rankForPlayer: Map<DwitchPlayerId, DwitchRank>
    ) {
        (ongoingGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
            .setInstance(DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer))
    }

    protected fun initializeNewRoundCardDealer(cardsForPlayer: Map<DwitchPlayerId, Set<Card>>) {
        (ongoingGameComponent.cardDealerFactory as DeterministicCardDealerFactory)
            .setInstance(DeterministicCardDealer(cardsForPlayer))
    }

    protected fun dudeWaitASec(seconds: Long = 2L) {
        Completable.fromAction { Logger.info { "Waiting for $seconds seconds..." } }
            .delay(seconds, TimeUnit.SECONDS)
            .blockingSubscribe()
    }

    protected fun dudeWaitAMillisSec(ms: Long = 500L) {
        Completable.fromAction { Logger.info { "Waiting for $ms milliseconds..." } }
            .delay(ms, TimeUnit.MILLISECONDS)
            .blockingSubscribe()
    }

    protected fun assertCurrentScreenIsHomeSreen() {
        testRule.onNodeWithText(getString(R.string.advertised_games)).assertExists()
        testRule.onNodeWithText(getString(R.string.resumable_games)).assertExists()
        testRule.onNodeWithText(getString(R.string.create_game)).assertExists()
    }

    protected fun buildSerializedAdvertisedGame(
        isNew: Boolean,
        gameName: String,
        gameCommonId: GameCommonId,
        gamePort: Int
    ): String {
        return "{\"isNew\": $isNew, \"gameCommonId\":{\"value\":${gameCommonId.value}},\"gameName\":\"$gameName\",\"gamePort\":$gamePort}"
    }

    protected fun getString(resource: Int): String {
        return res.getString(resource)
    }
}
