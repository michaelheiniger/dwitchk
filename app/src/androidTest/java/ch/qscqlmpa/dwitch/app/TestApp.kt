package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.DaggerTestAppComponent
import ch.qscqlmpa.dwitch.TestAppComponent
import ch.qscqlmpa.dwitch.TestIdlingResource
import ch.qscqlmpa.dwitch.ingame.InGameUiModule
import ch.qscqlmpa.dwitchcommunication.di.CommunicationModule
import ch.qscqlmpa.dwitchcommunication.di.DaggerTestCommunicationComponent
import ch.qscqlmpa.dwitchgame.di.DaggerTestGameComponent
import ch.qscqlmpa.dwitchgame.di.TestGameComponent
import ch.qscqlmpa.dwitchgame.di.modules.DwitchGameModule
import ch.qscqlmpa.dwitchgame.di.modules.StoreModule
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameModule
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
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
    lateinit var testGameComponent: TestGameComponent
    lateinit var testAppComponent: TestAppComponent

    val gameIdlingResource = TestIdlingResource("gameIdlingResource")

    private val testAppEventRelay = BehaviorRelay.create<TestAppEvent>()

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        testStoreComponent = DaggerTestStoreComponent.factory().create(TestStoreModule(this))

        testGameComponent = DaggerTestGameComponent.factory().create(
            DwitchGameModule(gameIdlingResource),
            StoreModule(testStoreComponent.store)
        )

        testAppComponent = DaggerTestAppComponent.builder()
            .applicationModule(ApplicationModule(this, gameIdlingResource))
            .gameComponent(testGameComponent)
            .build()
        return testAppComponent
    }

    override fun createInGameComponents(
        playerRole: PlayerRole,
        roomType: RoomType,
        gameLocalId: Long,
        localPlayerLocalId: Long,
        hostPort: Int,
        hostIpAddress: String
    ): InGameComponent? {
        Logger.debug { "startOngoingGame()" }
        if (inGameComponent == null) {
            inGameStoreComponent = testStoreComponent.addInGameStoreComponent(
                InGameStoreModule(gameLocalId, localPlayerLocalId)
            )

            communicationComponent = DaggerTestCommunicationComponent.factory()
                .create(CommunicationModule(hostIpAddress, hostPort, gameIdlingResource))

            inGameComponent = testGameComponent.addTestInGameComponent(
                InGameModule(
                    playerRole,
                    roomType,
                    gameLocalId,
                    localPlayerLocalId,
                    hostPort,
                    hostIpAddress,
                    inGameStoreComponent!!.inGameStore,
                    communicationComponent!!
                ),
            )
            inGameUiComponent = testAppComponent.addInGameUiComponent(
                InGameUiModule(
                    inGameComponent!!.gameFacade,
                    inGameComponent!!.hostGameFacade,
                    inGameComponent!!.guestGameFacade,
                    inGameComponent!!.waitingRoomFacade,
                    inGameComponent!!.waitingRoomHostFacade,
                    inGameComponent!!.waitingRoomGuestFacade,
                    inGameComponent!!.gameRoomHostFacade,
                    inGameComponent!!.gameRoomGuestFacade,
                    inGameComponent!!.playerFacade
                )
            )
            testAppEventRelay.accept(TestAppEvent.GameCreated)
        } else {
            Logger.warn { "startOngoingGame() called when a game is already on-going." }
        }
        return inGameComponent
    }

    override fun homeFacade(): HomeFacade {
        return testGameComponent.homeFacade
    }

    fun appEventRepository(): AppEventRepository {
        return testAppComponent.appEventRepository
    }
}
