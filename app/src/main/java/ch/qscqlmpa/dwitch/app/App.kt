package ch.qscqlmpa.dwitch.app

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import ch.qscqlmpa.dwitch.MainActivityComponent
import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ingame.services.GuestInGameService
import ch.qscqlmpa.dwitch.ingame.services.HostInGameService
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.qrcodescanner.QrCodeScannerActivity
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.di.*
import ch.qscqlmpa.dwitchgame.di.DaggerGameComponent
import ch.qscqlmpa.dwitchgame.di.GameComponent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameGuestComponent
import ch.qscqlmpa.dwitchgame.ingame.di.InGameHostComponent
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchstore.DaggerStoreComponent
import ch.qscqlmpa.dwitchstore.StoreComponent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStoreComponent
import org.tinylog.kotlin.Logger
import javax.inject.Inject

// TODO: For first production release, disable crashlytics and add opt-in in settings. See https://firebase.google.com/docs/crashlytics/customize-crash-reports?authuser=0&platform=android
open class App : Application() {

    lateinit var communicationComponent: CommunicationComponent
    lateinit var storeComponent: StoreComponent
    lateinit var gameComponent: GameComponent
    lateinit var appComponent: AppComponent

    var inGameHostCommunicationComponent: InGameHostCommunicationComponent? = null
    var inGameGuestCommunicationComponent: InGameGuestCommunicationComponent? = null
    var inGameStoreComponent: InGameStoreComponent? = null
    var inGameHostComponent: InGameHostComponent? = null
    var inGameGuestComponent: InGameGuestComponent? = null

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
            communicationComponent,
            storeComponent
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
        inGameStoreComponent = createInGameStoreComponent(gameLocalId, localPlayerLocalId)

        inGameHostCommunicationComponent = DaggerInGameHostCommunicationComponent.factory().create(StubIdlingResource())

        inGameHostComponent = gameComponent.getInGameHostComponentFactory().create(
            inGameStoreComponent!!.inGameStore,
            inGameHostCommunicationComponent!!.commServer,
            inGameHostCommunicationComponent!!.connectionStore
        )
    }

    open fun createInGameGuestComponents(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        advertisedGame: GameAdvertisingInfo
    ) {
        Logger.debug { "createInGameGuestComponents()" }
        inGameStoreComponent = createInGameStoreComponent(gameLocalId, localPlayerLocalId)

        inGameGuestCommunicationComponent = DaggerInGameGuestCommunicationComponent.factory().create(StubIdlingResource())

        inGameGuestComponent = gameComponent.getInGameGuestComponentFactory().create(
            advertisedGame,
            inGameStoreComponent!!.inGameStore,
            inGameGuestCommunicationComponent!!.commClient,
            inGameGuestCommunicationComponent!!.connectionStore
        )
    }

    protected fun createInGameStoreComponent(gameLocalId: Long, localPlayerLocalId: Long): InGameStoreComponent {
        return storeComponent.getInGameStoreComponentFactory().create(gameLocalId, localPlayerLocalId)
    }

    fun createMainActivityComponent(): MainActivityComponent {
        Logger.debug { "createMainActivityComponent" }
        return appComponent.mainActivityComponentFactory().create()
    }

    fun createInGameHostUiComponent(component: MainActivityComponent): InGameHostUiComponent {
        Logger.debug { "createInGameHostUiComponent" }
        return component.getInGameHostUiComponentFactory().create(
            inGameHostComponent!!.gameAdvertisingFacade,
            inGameHostComponent!!.hostCommunicationFacade,
            inGameHostComponent!!.waitingRoomFacade,
            inGameHostComponent!!.waitingRoomHostFacade,
            inGameHostComponent!!.inGameHostFacade,
            inGameHostComponent!!.playerFacade
        )
    }

    fun createInGameGuestUiComponent(component: MainActivityComponent): InGameGuestUiComponent {
        Logger.debug { "createInGameGuestUiComponent" }
        return component.getInGameGuestUiComponentFactory().create(
            inGameGuestComponent!!.guestCommunicationFacade,
            inGameGuestComponent!!.waitingRoomFacade,
            inGameGuestComponent!!.waitingRoomGuestFacade,
            inGameGuestComponent!!.inGameGuestFacade,
            inGameGuestComponent!!.playerFacade
        )
    }

    fun destroyInGameComponents() {
        Logger.debug { "destroyInGameComponents()" }
        inGameHostComponent = null
        inGameGuestComponent = null
        inGameStoreComponent = null
    }

    fun inject(activity: QrCodeScannerActivity) {
        appComponent.inject(activity)
    }

    fun inject(service: HostInGameService) {
        appComponent.inject(service)
    }

    fun inject(service: GuestInGameService) {
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
