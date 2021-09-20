package ch.qscqlmpa.dwitch.e2e.base

import android.content.res.Resources
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.HomeActivity
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.TestApp
import ch.qscqlmpa.dwitch.e2e.DisableAnimationsRule
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.di.TestInGameGuestCommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.TestInGameHostCommunicationComponent
import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.ServerTestStub
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetup
import ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic.DeterministicInitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.Packet
import ch.qscqlmpa.dwitchgame.gamediscovery.lan.network.TestNetworkAdapter
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameComponent
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.TestInGameHostComponent
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.store.Store
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
abstract class BaseE2eTest {

    @get:Rule
    val testRule = createAndroidComposeRule<HomeActivity>()

    @get:Rule
    val animationsRule = DisableAnimationsRule()

    protected val gameName = "LOTR"

    protected val hostName = "Aragorn"

    private lateinit var res: Resources

    private lateinit var storeComponent: TestStoreComponent
    private lateinit var gameComponent: TestGameComponent

    private lateinit var inGameComponent: TestInGameComponent
    private lateinit var inGameHostComponent: TestInGameHostComponent
    private lateinit var inGameGuestComponent: TestInGameGuestComponent
    private lateinit var communicationHostComponent: TestInGameHostCommunicationComponent
    private lateinit var communicationGuestComponent: TestInGameGuestCommunicationComponent

    protected lateinit var networkAdapter: TestNetworkAdapter

    private lateinit var store: Store
    protected lateinit var inGameStore: InGameStore

    protected lateinit var commSerializerFactory: InGameSerializerFactory

    protected lateinit var serverTestStub: ServerTestStub
    protected lateinit var clientTestStub: ClientTestStub

    lateinit var app: TestApp

    private lateinit var gameIdlingResource: IdlingResourceAdapter

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        res = InstrumentationRegistry.getInstrumentation().targetContext.resources
        gameIdlingResource = IdlingResourceAdapter(app.gameIdlingResource)
        testRule.registerIdlingResource(gameIdlingResource)

        gameComponent = app.testGameComponent
        storeComponent = app.testStoreComponent
        store = storeComponent.store
        networkAdapter = gameComponent.networkListener as TestNetworkAdapter
    }

    @After
    fun tearDown() {
        testRule.unregisterIdlingResource(gameIdlingResource)
    }

    protected fun assertCurrentScreenIsHomeSreen() {
        testRule.onNodeWithText(getString(R.string.advertised_games)).assertExists()
        testRule.onNodeWithText(getString(R.string.create_new_game)).assertExists()
    }

    protected fun advertiseGame(
        isNew: Boolean,
        gameName: String,
        gameCommonId: GameCommonId,
        gamePort: Int,
        senderIpAddress: String,
        senderPort: Int
    ) {
        val ad =
            "{\"isNew\": $isNew, \"gameCommonId\":\"${gameCommonId.value}\",\"gameName\":\"$gameName\",\"gamePort\":$gamePort}"
        incrementGameIdlingResource("Advertise game $ad")
        networkAdapter.setPacket(Packet(ad, senderIpAddress, senderPort))
    }

    protected fun initializeInitialGameSetup(
        cardsForPlayer: Map<DwitchPlayerId, Set<Card>>,
        rankForPlayer: Map<DwitchPlayerId, DwitchRank>
    ) {
        (inGameComponent.initialGameSetupFactory as DeterministicInitialGameSetupFactory)
            .setInstance(DeterministicInitialGameSetup(cardsForPlayer, rankForPlayer))
    }

    protected fun initializeNewRoundCardDealer(cardsForPlayer: Map<DwitchPlayerId, Set<Card>>) {
        (inGameComponent.cardDealerFactory as DeterministicCardDealerFactory)
            .setInstance(DeterministicCardDealer(cardsForPlayer))
    }

    protected fun getString(resource: Int): String {
        return res.getString(resource)
    }

    protected fun incrementGameIdlingResource(reason: String) {
        gameIdlingResource.increment(reason)
    }

    protected fun decrementGameIdlingResource(reason: String) {
        gameIdlingResource.decrement(reason)
    }

    protected fun waitUntilGuestIsConnected() {
        inGameGuestComponent.guestCommunicationFacade.currentCommunicationState()
            .filter { state -> state is GuestCommunicationState.Connected }
            .timeout(5, TimeUnit.SECONDS)
            .blockingFirst()
    }

    protected fun waitForServiceToBeStarted() {
        app.appEventRepository.observeEvents().filter { event -> event is AppEvent.ServiceStarted }.blockingFirst()
    }

    protected fun hookOngoingGameDependenciesForHost() {
        hookOngoingGameDependenciesCommon()
        communicationHostComponent = app.inGameHostCommunicationComponent as TestInGameHostCommunicationComponent
        inGameHostComponent = app.inGameHostComponent as TestInGameHostComponent
        inGameComponent = inGameHostComponent
        commSerializerFactory = communicationHostComponent.serializerFactory
        serverTestStub = communicationHostComponent.serverTestStub
    }

    protected fun hookOngoingGameDependenciesForGuest() {
        hookOngoingGameDependenciesCommon()
        communicationGuestComponent = app.inGameGuestCommunicationComponent as TestInGameGuestCommunicationComponent
        inGameGuestComponent = app.inGameGuestComponent as TestInGameGuestComponent
        inGameComponent = inGameGuestComponent
        commSerializerFactory = communicationGuestComponent.serializerFactory
        clientTestStub = communicationGuestComponent.clientTestStub.get()
    }

    private fun hookOngoingGameDependenciesCommon() {
        inGameStore = app.inGameStoreComponent!!.inGameStore
    }
}

class IdlingResourceAdapter(private val idlingResource: DwitchIdlingResource) : IdlingResource {
    override val isIdleNow: Boolean
        get() = idlingResource.isIdleNow()

    fun increment(reason: String) {
        idlingResource.increment(reason)
    }

    fun decrement(reason: String) {
        idlingResource.decrement(reason)
    }
}
