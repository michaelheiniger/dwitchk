package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameGuestUiModule
import ch.qscqlmpa.dwitch.ingame.InGameHostUiComponent
import ch.qscqlmpa.dwitch.ingame.InGameHostUiModule
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.CommunicationModule
import ch.qscqlmpa.dwitchcommunication.di.DaggerCommunicationComponent
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
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
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
    var communicationComponent: CommunicationComponent? = null
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

    open fun createInGameComponents(
        playerRole: PlayerRole,
        gameLocalId: Long,
        localPlayerLocalId: Long,
        hostPort: Int,
        hostIpAddress: String
    ) {
        Logger.debug { "startOngoingGame()" }
        inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

        communicationComponent = DaggerCommunicationComponent.factory()
            .create(CommunicationModule(hostIpAddress, hostPort, StubIdlingResource()))

        when (playerRole) {
            PlayerRole.GUEST -> {
                inGameGuestComponent = gameComponent.addInGameGuestComponent(
                    InGameGuestModule(
                        gameLocalId,
                        localPlayerLocalId,
                        hostPort,
                        hostIpAddress,
                        inGameStoreComponent!!.inGameStore,
                        communicationComponent!!.commClient,
                        communicationComponent!!.connectionStore
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
            PlayerRole.HOST -> {
                inGameHostComponent = gameComponent.addInGameHostComponent(
                    InGameHostModule(
                        gameLocalId,
                        localPlayerLocalId,
                        inGameStoreComponent!!.inGameStore,
                        communicationComponent!!.commServer,
                        communicationComponent!!.connectionStore,
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
        }
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
