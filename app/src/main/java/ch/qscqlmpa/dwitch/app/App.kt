package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ingame.OnGoingGameUiModule
import ch.qscqlmpa.dwitch.ingame.OngoingGameUiComponent
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.CommunicationModule
import ch.qscqlmpa.dwitchcommunication.di.DaggerCommunicationComponent
import ch.qscqlmpa.dwitchgame.di.DaggerGameComponent
import ch.qscqlmpa.dwitchgame.di.GameComponent
import ch.qscqlmpa.dwitchgame.di.modules.DwitchGameModule
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.ingame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ingame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ingame.di.InGameComponent
import ch.qscqlmpa.dwitchgame.ingame.di.modules.InGameModule
import ch.qscqlmpa.dwitchmodel.game.RoomType
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

open class App : DaggerApplication() {

    private lateinit var storeComponent: StoreComponent
    private lateinit var gameComponent: GameComponent
    private lateinit var appComponent: AppComponent
    var communicationComponent: CommunicationComponent? = null
    var inGameStoreComponent: InGameStoreComponent? = null
    var ongoingGameUiComponent: OngoingGameUiComponent? = null
    var inGameComponent: InGameComponent? = null

    @Inject
    lateinit var serviceManager: ServiceManager

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        storeComponent = DaggerStoreComponent.factory().create(StoreModule(this))

        gameComponent = DaggerGameComponent.factory().create(
            DwitchGameModule(ProdIdlingResource()),
            ch.qscqlmpa.dwitchgame.di.modules.StoreModule(storeComponent.store)
        )

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .applicationModule(ApplicationModule(this, ProdIdlingResource()))
            .gameComponent(gameComponent)
            .build()
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
        serviceManager.init()
    }

    open fun startOngoingGame(
        playerRole: PlayerRole,
        roomType: RoomType,
        gameLocalId: Long,
        localPlayerLocalId: Long,
        hostPort: Int,
        hostIpAddress: String
    ): InGameComponent? {
        Logger.debug { "startOngoingGame()" }
        if (inGameComponent == null) {
            inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

            communicationComponent = DaggerCommunicationComponent.factory()
                .create(CommunicationModule(hostIpAddress, hostPort, ProdIdlingResource()))

            inGameComponent = gameComponent.addInGameComponent(
                InGameModule(
                    playerRole,
                    roomType,
                    gameLocalId,
                    localPlayerLocalId,
                    hostPort,
                    hostIpAddress,
                    inGameStoreComponent!!.inGameStore,
                    communicationComponent!!
                )
            )
            ongoingGameUiComponent = appComponent.addOngoingGameUiComponent(
                OnGoingGameUiModule(
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
        } else {
            Logger.warn { "startOngoingGame() called when a game is already on-going." }
        }
        return inGameComponent
    }

    fun hostFacade(): HostGameFacade {
        checkNotNull(inGameComponent) { "No on-going game component!" }
        return inGameComponent!!.hostGameFacade
    }

    fun guestFacade(): GuestGameFacade {
        checkNotNull(inGameComponent) { "No on-going game component!" }
        return inGameComponent!!.guestGameFacade
    }

    open fun homeFacade(): HomeFacade {
        return gameComponent.homeFacade
    }

    fun getGameUiComponent(): OngoingGameUiComponent? {
        checkNotNull(inGameComponent) { "No on-going game ui component!" }
        return ongoingGameUiComponent
    }

    fun stopOngoingGame() {
        ongoingGameUiComponent = null
        inGameComponent = null
        inGameStoreComponent = null
    }

    private fun createNotificationChannels() {
        NotificationChannelFactory.createDefaultNotificationChannel(this)
    }
}
