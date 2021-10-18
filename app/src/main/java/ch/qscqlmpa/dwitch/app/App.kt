package ch.qscqlmpa.dwitch.app

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import ch.qscqlmpa.dwitch.MainActivity
import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.ingame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ingame.services.HostInGameService
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.di.*
import ch.qscqlmpa.dwitchgame.di.DaggerGameComponent
import ch.qscqlmpa.dwitchgame.di.GameComponent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.InGameHostComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameGuestModule
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameHostModule
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchstore.DaggerStoreComponent
import ch.qscqlmpa.dwitchstore.StoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreModule
import org.tinylog.kotlin.Logger
import javax.inject.Inject

// TODO: For first production release, disable crashlytics and add opt-in in settings. See https://firebase.google.com/docs/crashlytics/customize-crash-reports?authuser=0&platform=android
open class App : Application() {

    private lateinit var storeComponent: StoreComponent
    private lateinit var gameComponent: GameComponent
    private lateinit var appComponent: AppComponent
    private lateinit var communicationComponent: CommunicationComponent

    var inGameHostCommunicationComponent: InGameHostCommunicationComponent? = null
    var inGameGuestCommunicationComponent: InGameGuestCommunicationComponent? = null
    var inGameStoreComponent: InGameStoreComponent? = null
    var inGameHostUiComponent: InGameHostUiComponent? = null
    var inGameGuestUiComponent: InGameGuestUiComponent? = null
    var inGameHostComponent: InGameHostComponent? = null
    var inGameGuestComponent: InGameGuestComponent? = null
    var inGameViewModelFactory: ViewModelFactory? = null

    @Inject
    lateinit var serviceManager: ServiceManager

    override fun onCreate() {
        super.onCreate()

        createDaggerComponents()

        createNotificationChannels()
    }

    protected open fun createDaggerComponents() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        communicationComponent = DaggerCommunicationComponent.factory().create(connectivityManager)

        storeComponent = DaggerStoreComponent.factory().create(this)

        gameComponent = DaggerGameComponent.factory().create(
            StubIdlingResource(),
            storeComponent.store,
            communicationComponent.gameDiscovery,
            communicationComponent.deviceConnectivityRepository
        )

        appComponent = DaggerAppComponent.factory().create(
            this,
            StubIdlingResource(),
            gameComponent
        )
    }

    open fun createInGameHostComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long
    ) {
        Logger.debug { "createInGameHostComponents()" }
        inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

        inGameHostCommunicationComponent = DaggerInGameHostCommunicationComponent.factory()
            .create(CommunicationHostModule(StubIdlingResource()))

        inGameHostComponent = gameComponent.addInGameHostComponent(
            InGameHostModule(
                gameLocalId,
                localPlayerLocalId,
                communicationComponent.gameAdvertiser,
                inGameStoreComponent!!.inGameStore,
                inGameHostCommunicationComponent!!.commServer,
                inGameHostCommunicationComponent!!.connectionStore
            )
        )
        inGameHostUiComponent = appComponent.addInGameHostUiComponent(
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
    }

    open fun createInGameGuestComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        advertisedGame: GameAdvertisingInfo
    ) {
        Logger.debug { "createInGameGuestComponents()" }
        inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

        inGameGuestCommunicationComponent = DaggerInGameGuestCommunicationComponent.factory()
            .create(CommunicationGuestModule(StubIdlingResource()))

        inGameGuestComponent = gameComponent.addInGameGuestComponent(
            InGameGuestModule(
                gameLocalId,
                localPlayerLocalId,
                advertisedGame,
                inGameStoreComponent!!.inGameStore,
                inGameGuestCommunicationComponent!!.commClient,
                inGameGuestCommunicationComponent!!.connectionStore
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

    open fun inject(activity: MainActivity) {
        appComponent.inject(activity)
    }

    open fun inject(activity: QrCodeScannerActivity) {
        appComponent.inject(activity)
    }

    open fun inject(service: HostInGameService) {
        appComponent.inject(service)
    }

    open fun inject(service: GuestInGameService) {
        appComponent.inject(service)
    }

    open val gameLifecycleFacade get(): GameLifecycleFacade = gameComponent.gameLifecycleFacade

    val gameAdvertisingFacade get(): GameAdvertisingFacade = inGameHostComponent!!.gameAdvertisingFacade

    val hostCommunicationFacade
        get(): HostCommunicationFacade? {
            return inGameHostComponent?.hostCommunicationFacade
        }

    val guestCommunicationFacade
        get(): GuestCommunicationFacade? {
            return inGameGuestComponent?.guestCommunicationFacade
        }

    private fun createNotificationChannels() {
        NotificationChannelFactory.createDefaultNotificationChannel(this)
    }
}
