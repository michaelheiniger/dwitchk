package ch.qscqlmpa.dwitch.e2e.base

import android.content.res.Resources
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.TestApp
import ch.qscqlmpa.dwitch.e2e.DisableAnimationsRule
import ch.qscqlmpa.dwitch.ui.home.main.MainActivity
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
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
import ch.qscqlmpa.dwitchgame.gamediscovery.network.Packet
import ch.qscqlmpa.dwitchgame.gamediscovery.network.TestNetworkAdapter
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.di.TestOngoingGameComponent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
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
abstract class BaseUiTest {

    @get:Rule
    val testRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val animationsRule = DisableAnimationsRule()

    protected val gameName = "LOTR"

    protected val hostName = "Aragorn"

    private lateinit var res: Resources

    private lateinit var storeComponent: TestStoreComponent
    private lateinit var gameComponent: TestGameComponent

    private lateinit var ongoingGameComponent: TestOngoingGameComponent
    private lateinit var communicationComponent: TestCommunicationComponent

    protected lateinit var networkAdapter: TestNetworkAdapter

    private lateinit var store: Store
    protected lateinit var inGameStore: InGameStore

    protected lateinit var commSerializerFactory: SerializerFactory

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
        testRule.onNodeWithText(getString(R.string.create_game)).assertExists()
    }

    protected fun advertiseGame(
        isNew: Boolean,
        gameName: String,
        gameCommonId: GameCommonId,
        gamePort: Int,
        senderIpAddress: String,
        senderPort: Int
    ) {
        incrementGameIdlingResource()
        val ad =
            "{\"isNew\": $isNew, \"gameCommonId\":{\"value\":${gameCommonId.value}},\"gameName\":\"$gameName\",\"gamePort\":$gamePort}"
        networkAdapter.setPacket(Packet(ad, senderIpAddress, senderPort))
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

    protected fun getString(resource: Int): String {
        return res.getString(resource)
    }

    protected fun incrementGameIdlingResource() {
        gameIdlingResource.increment()
    }

    protected fun decrementGameIdlingResource() {
        gameIdlingResource.decrement()
    }

    /**
     * Used to wait for game data update before letting Compose check the screen.
     * This can't easily be done with idling resource since it is complicated
     * to determine when game data is updated (since it is often updated).
     * Not the ideal solution but a pragmatic one.
     */
    protected fun waitUntilPlayerDashboardIsUpdated() {
        ongoingGameComponent.gameFacade.observeGameData()
            .filter { data -> data is DwitchState.RoundIsBeginning || data is DwitchState.RoundIsOngoing }
            .blockingFirst()
    }

    protected fun waitUntilGuestIsConnected() {
        ongoingGameComponent.guestGameFacade.currentCommunicationState()
            .filter { state -> state is GuestCommunicationState.Connected }
            .timeout(5, TimeUnit.SECONDS)
            .blockingFirst()
    }

    protected fun waitForServiceToBeStarted() {
        app.appEventRepository().observeEvents().filter { event -> event is AppEvent.ServiceStarted }.blockingFirst()
    }

    protected fun hookOngoingGameDependenciesForHost() {
        hookOngoingGameDependenciesCommon()
        commSerializerFactory = communicationComponent.serializerFactory
        serverTestStub = communicationComponent.serverTestStub
    }

    protected fun hookOngoingGameDependenciesForGuest() {
        hookOngoingGameDependenciesCommon()
        commSerializerFactory = communicationComponent.serializerFactory
        clientTestStub = communicationComponent.clientTestStub.get()
    }

    private fun hookOngoingGameDependenciesCommon() {
        ongoingGameComponent = app.ongoingGameComponent as TestOngoingGameComponent
        inGameStore = app.inGameStoreComponent!!.inGameStore
        communicationComponent = app.communicationComponent as TestCommunicationComponent
    }
}

class IdlingResourceAdapter(private val idlingResource: DwitchIdlingResource) : IdlingResource {
    override val isIdleNow: Boolean
        get() = idlingResource.isIdleNow()

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        idlingResource.decrement()
    }
}
