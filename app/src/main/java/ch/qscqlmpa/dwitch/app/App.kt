package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchcommunication.di.CommunicationGuestComponent
import ch.qscqlmpa.dwitchcommunication.di.CommunicationGuestModule
import ch.qscqlmpa.dwitchcommunication.di.CommunicationHostComponent
import ch.qscqlmpa.dwitchcommunication.di.CommunicationHostModule
import ch.qscqlmpa.dwitchcommunication.di.DaggerCommunicationGuestComponent
import ch.qscqlmpa.dwitchcommunication.di.DaggerCommunicationHostComponent
import ch.qscqlmpa.dwitchgame.di.DaggerGameComponent
import ch.qscqlmpa.dwitchgame.di.GameComponent
import ch.qscqlmpa.dwitchgame.di.modules.DwitchGameModule
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.InGameHostComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameGuestModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameHostModule
import ch.qscqlmpa.dwitchstore.DaggerStoreComponent
import ch.qscqlmpa.dwitchstore.StoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreModule
import ch.qscqlmpa.dwitchstore.store.StoreModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import org.tinylog.kotlin.Logger
import javax.inject.Inject

//TODO: For first production release, disable crashlytics and add opt-in in settings. See https://firebase.google.com/docs/crashlytics/customize-crash-reports?authuser=0&platform=android
open class App : DaggerApplication() {

    private lateinit var storeComponent: StoreComponent
    private lateinit var gameComponent: GameComponent
    private lateinit var appComponent: AppComponent
    var communicationHostComponent: CommunicationHostComponent? = null
    var communicationGuestComponent: CommunicationGuestComponent? = null
    var inGameStoreComponent: InGameStoreComponent? = null
    var inGameHostUiComponent: InGameHostUiComponent? = null
    var inGameGuestUiComponent: InGameGuestUiComponent? = null
    var inGameHostComponent: InGameHostComponent? = null
    var inGameGuestComponent: InGameGuestComponent? = null
    var inGameViewModelFactory: ViewModelFactory? = null

    @Inject
    lateinit var serviceManager: ServiceManager

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        storeComponent = DaggerStoreComponent.factory().create(StoreModule(this))

        gameComponent = DaggerGameComponent.factory().create(
            DwitchGameModule(StubIdlingResource()),
            ch.qscqlmpa.dwitchgame.di.modules.StoreModule(storeComponent.store)
        )

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .applicationModule(ApplicationModule(this, StubIdlingResource()))
            .gameComponent(gameComponent)
            .build()
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    open fun createInGameHostComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long
    ) {
        Logger.debug { "createInGameHostComponents()" }
        inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

        communicationHostComponent = DaggerCommunicationHostComponent.factory()
            .create(CommunicationHostModule("0.0.0.0", 8889, StubIdlingResource()))

        inGameHostComponent = gameComponent.addInGameHostComponent(
            InGameHostModule(
                gameLocalId,
                localPlayerLocalId,
                inGameStoreComponent!!.inGameStore,
                communicationHostComponent!!.commServer,
                communicationHostComponent!!.connectionStore,
            )
        )
        inGameHostUiComponent = appComponent.addInGameHostUiComponent(
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
    }

    open fun createInGameGuestComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        hostPort: Int,
        hostIpAddress: String
    ) {
        Logger.debug { "createInGameGuestComponents()" }
        inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

        communicationGuestComponent = DaggerCommunicationGuestComponent.factory()
            .create(CommunicationGuestModule(hostIpAddress, hostPort, StubIdlingResource()))

        inGameGuestComponent = gameComponent.addInGameGuestComponent(
            InGameGuestModule(
                gameLocalId,
                localPlayerLocalId,
                hostPort,
                hostIpAddress,
                inGameStoreComponent!!.inGameStore,
                communicationGuestComponent!!.commClient,
                communicationGuestComponent!!.connectionStore
            )
        )
        inGameGuestUiComponent = appComponent.addInGameGuestUiComponent(
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
    }

    fun destroyInGameComponents() {
        Logger.debug { "destroyInGameComponents()" }
        inGameHostUiComponent = null
        inGameGuestUiComponent = null
        inGameHostComponent = null
        inGameGuestComponent = null
        inGameStoreComponent = null
        inGameViewModelFactory = null
    }

    open val gameLifecycleFacade get(): GameLifecycleFacade = gameComponent.gameLifecycleFacade

    open val gameAdvertisingFacade get(): GameAdvertisingFacade = gameComponent.gameAdvertisingFacade

    val hostCommunicationFacade
        get(): HostCommunicationFacade {
            checkNotNull(inGameHostComponent) { "No in-game component" }
            return inGameHostComponent!!.hostCommunicationFacade
        }

    val guestCommunicationFacade
        get(): GuestCommunicationFacade {
            checkNotNull(inGameGuestComponent) { "No in-game component" }
            return inGameGuestComponent!!.guestCommunicationFacade
        }

    private fun createNotificationChannels() {
        NotificationChannelFactory.createDefaultNotificationChannel(this)
    }
}
