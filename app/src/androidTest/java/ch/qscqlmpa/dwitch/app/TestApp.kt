package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.DaggerTestAppComponent
import ch.qscqlmpa.dwitch.MainActivity
import ch.qscqlmpa.dwitch.TestAppComponent
import ch.qscqlmpa.dwitch.TestIdlingResource
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.ingame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ingame.services.HostInGameService
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.di.*
import ch.qscqlmpa.dwitchgame.di.DaggerTestGameComponent
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameGuestModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameHostModule
import ch.qscqlmpa.dwitchstore.DaggerTestStoreComponent
import ch.qscqlmpa.dwitchstore.TestStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreModule
import com.jakewharton.rxrelay3.BehaviorRelay
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

    override fun createDaggerComponents() {
        testCommunicationComponent = DaggerTestCommunicationComponent.factory().create(this)

        testStoreComponent = DaggerTestStoreComponent.factory().create(this)

        testGameComponent = DaggerTestGameComponent.factory().create(
            gameIdlingResource,
            testStoreComponent.store,
            testCommunicationComponent.gameDiscovery,
            testCommunicationComponent.deviceConnectivityRepository
        )

        testAppComponent = DaggerTestAppComponent.factory().create(
            this,
            gameIdlingResource,
            testGameComponent
        )
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
                inGameHostComponent!!.gameAdvertisingFacade,
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

    override fun inject(activity: MainActivity) {
        testAppComponent.inject(activity)
    }

    override fun inject(activity: QrCodeScannerActivity) {
        testAppComponent.inject(activity)
    }

    override fun inject(service: HostInGameService) {
        testAppComponent.inject(service)
    }

    override fun inject(service: GuestInGameService) {
        testAppComponent.inject(service)
    }

    override val gameLifecycleFacade get(): GameLifecycleFacade = testGameComponent.gameLifecycleFacade

    val appEventRepository get(): AppEventRepository = testAppComponent.appEventRepository
}
