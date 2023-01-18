package ch.qscqlmpa.dwitch.app

import android.content.Context
import android.net.ConnectivityManager
import ch.qscqlmpa.dwitch.TestIdlingResource
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.di.DaggerTestCommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.DaggerTestInGameGuestCommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.DaggerTestInGameHostCommunicationComponent
import ch.qscqlmpa.dwitchgame.di.DaggerTestGameComponent
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchstore.DaggerTestStoreComponent
import com.jakewharton.rxrelay3.BehaviorRelay
import org.tinylog.kotlin.Logger

sealed class TestAppEvent {
    object GameCreated : TestAppEvent()
}

class TestApp : App() {

    lateinit var testGameComponent: TestGameComponent

    val gameIdlingResource = TestIdlingResource("gameIdlingResource")

    private val testAppEventRelay = BehaviorRelay.create<TestAppEvent>()

    override fun createDaggerComponents() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        communicationComponent = DaggerTestCommunicationComponent.factory().create(connectivityManager)

        storeComponent = DaggerTestStoreComponent.factory().create(this)

        testGameComponent = DaggerTestGameComponent.factory().create(
            gameIdlingResource,
            communicationComponent,
            storeComponent
        )

        appComponent = DaggerAppComponent.factory().create(
            this,
            gameIdlingResource,
            testGameComponent
        )
    }

    override fun createInGameHostComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long
    ) {
        Logger.debug { "createInGameHostComponents()" }
        inGameStoreComponent = createInGameStoreComponent(gameLocalId, localPlayerLocalId)

        inGameHostCommunicationComponent = DaggerTestInGameHostCommunicationComponent.factory().create(gameIdlingResource)

        inGameHostComponent = testGameComponent.getTestInGameHostComponentFactory().create(
            inGameStoreComponent!!.inGameStore,
            inGameHostCommunicationComponent!!.commServer,
            inGameHostCommunicationComponent!!.connectionStore,
        )

        testAppEventRelay.accept(TestAppEvent.GameCreated)
    }

    override fun createInGameGuestComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        advertisedGame: GameAdvertisingInfo
    ) {
        Logger.debug { "createInGameGuestComponents()" }
        inGameStoreComponent = createInGameStoreComponent(gameLocalId, localPlayerLocalId)

        inGameGuestCommunicationComponent = DaggerTestInGameGuestCommunicationComponent.factory().create(gameIdlingResource)

        inGameGuestComponent = testGameComponent.getTestInGameGuestComponentFactory().create(
            advertisedGame,
            inGameStoreComponent!!.inGameStore,
            inGameGuestCommunicationComponent!!.commClient,
            inGameGuestCommunicationComponent!!.connectionStore
        )

        testAppEventRelay.accept(TestAppEvent.GameCreated)
    }

    override val gameLifecycleFacade get(): GameLifecycleFacade = testGameComponent.gameLifecycleFacade

    val appEventRepository get(): AppEventRepository = appComponent.appEventRepository
}
