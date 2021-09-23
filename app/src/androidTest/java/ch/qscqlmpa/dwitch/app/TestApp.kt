package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.DaggerTestAppComponent
import ch.qscqlmpa.dwitch.TestAppComponent
import ch.qscqlmpa.dwitch.TestIdlingResource
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.di.*
import ch.qscqlmpa.dwitchgame.di.DaggerTestGameComponent
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.di.modules.DwitchGameModule
import ch.qscqlmpa.dwitchgame.di.modules.GameDiscoveryModule
import ch.qscqlmpa.dwitchgame.di.modules.StoreModule
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameGuestModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameHostModule
import ch.qscqlmpa.dwitchstore.DaggerTestStoreComponent
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreModule
import ch.qscqlmpa.dwitchstore.store.TestStoreModule
import com.jakewharton.rxrelay3.BehaviorRelay
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.tinylog.kotlin.Logger

sealed class TestAppEvent {
    object GameCreated : TestAppEvent()
}

class TestApp : App() {

    lateinit var testStoreComponent: TestStoreComponent
    lateinit var testCommunicationComponent: TestCommunicationComponent
    lateinit var testGameComponent: TestGameComponent
    lateinit var testAppComponent: TestAppComponent

    val gameIdlingResource = TestIdlingResource("gameIdlingResource")

    private val testAppEventRelay = BehaviorRelay.create<TestAppEvent>()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        testCommunicationComponent = DaggerTestCommunicationComponent.factory().create(CommunicationModule(this))

        testStoreComponent = DaggerTestStoreComponent.factory().create(TestStoreModule(this))

        testGameComponent = DaggerTestGameComponent.factory().create(
            DwitchGameModule(gameIdlingResource),
            StoreModule(testStoreComponent.store),
            GameDiscoveryModule(testCommunicationComponent.gameDiscovery)
        )

        testAppComponent = DaggerTestAppComponent.builder()
            .applicationModule(ApplicationModule(this, gameIdlingResource))
            .gameComponent(testGameComponent)
            .build()
        return testAppComponent
    }

    override fun createInGameHostComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long
    ) {
        Logger.debug { "startOngoingGame()" }
        inGameStoreComponent = testStoreComponent.addInGameStoreComponent(
            InGameStoreModule(gameLocalId, localPlayerLocalId)
        )

        inGameHostCommunicationComponent = DaggerTestInGameHostCommunicationComponent.factory()
            .create(CommunicationHostModule(gameIdlingResource))

        inGameHostComponent = testGameComponent.addTestInGameHostComponent(
            InGameHostModule(
                gameLocalId,
                localPlayerLocalId,
                testCommunicationComponent.gameAdvertiser,
                inGameStoreComponent!!.inGameStore,
                inGameHostCommunicationComponent!!.commServer,
                inGameHostCommunicationComponent!!.connectionStore,
            )
        )
        inGameHostUiComponent = testAppComponent.addInGameHostUiComponent(
            InGameHostUiModule(
                inGameHostComponent!!.gameFacadeToRename,
                inGameHostComponent!!.hostCommunicationFacade,
                inGameHostComponent!!.waitingRoomFacade,
                inGameHostComponent!!.waitingRoomHostFacade,
                inGameHostComponent!!.inGameHostFacade,
                inGameHostComponent!!.playerFacade
            )
        )
        inGameViewModelFactory = inGameHostUiComponent!!.viewModelFactory
        testAppEventRelay.accept(TestAppEvent.GameCreated)
    }

    override fun createInGameGuestComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        advertisedGame: GameAdvertisingInfo
    ) {
        Logger.debug { "startOngoingGame()" }
        inGameStoreComponent = testStoreComponent.addInGameStoreComponent(
            InGameStoreModule(gameLocalId, localPlayerLocalId)
        )

        inGameGuestCommunicationComponent = DaggerTestInGameGuestCommunicationComponent.factory()
            .create(CommunicationGuestModule(gameIdlingResource))

        inGameGuestComponent = testGameComponent.addTestInGameGuestComponent(
            InGameGuestModule(
                gameLocalId,
                localPlayerLocalId,
                advertisedGame,
                inGameStoreComponent!!.inGameStore,
                inGameGuestCommunicationComponent!!.commClient,
                inGameGuestCommunicationComponent!!.connectionStore
            )
        )
        inGameGuestUiComponent = testAppComponent.addInGameGuestUiComponent(
            InGameGuestUiModule(
                inGameGuestComponent!!.gameFacadeToRename,
                inGameGuestComponent!!.guestCommunicationFacade,
                inGameGuestComponent!!.waitingRoomFacade,
                inGameGuestComponent!!.waitingRoomGuestFacade,
                inGameGuestComponent!!.inGameGuestFacade,
                inGameGuestComponent!!.playerFacade
            )
        )
        inGameViewModelFactory = inGameGuestUiComponent!!.viewModelFactory
        testAppEventRelay.accept(TestAppEvent.GameCreated)
    }

    override val gameLifecycleFacade get(): GameLifecycleFacade = testGameComponent.gameLifecycleFacade

    val appEventRepository get(): AppEventRepository = testAppComponent.appEventRepository
}
