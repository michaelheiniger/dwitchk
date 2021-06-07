package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ongoinggame.OnGoingGameUiModule
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameUiComponent
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.CommunicationModule
import ch.qscqlmpa.dwitchcommunication.di.DaggerCommunicationComponent
import ch.qscqlmpa.dwitchgame.di.DaggerGameComponent
import ch.qscqlmpa.dwitchgame.di.GameComponent
import ch.qscqlmpa.dwitchgame.di.modules.DwitchGameModule
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.GuestGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.common.HostGameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameComponent
import ch.qscqlmpa.dwitchgame.ongoinggame.di.modules.OngoingGameModule
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
    var ongoingGameComponent: OngoingGameComponent? = null

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
    ): OngoingGameComponent? {
        Logger.debug { "startOngoingGame()" }
        if (ongoingGameComponent == null) {
            inGameStoreComponent = storeComponent.addInGameStoreComponent(InGameStoreModule(gameLocalId, localPlayerLocalId))

            communicationComponent = DaggerCommunicationComponent.factory()
                .create(CommunicationModule(hostIpAddress, hostPort, ProdIdlingResource()))

            ongoingGameComponent = gameComponent.addOngoingGameComponent(
                OngoingGameModule(
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
                    ongoingGameComponent!!.gameFacade,
                    ongoingGameComponent!!.hostGameFacade,
                    ongoingGameComponent!!.guestGameFacade,
                    ongoingGameComponent!!.waitingRoomFacade,
                    ongoingGameComponent!!.waitingRoomHostFacade,
                    ongoingGameComponent!!.waitingRoomGuestFacade,
                    ongoingGameComponent!!.gameRoomHostFacade,
                    ongoingGameComponent!!.gameRoomGuestFacade,
                    ongoingGameComponent!!.playerFacade
                )
            )
        } else {
            Logger.warn { "startOngoingGame() called when a game is already on-going." }
        }
        return ongoingGameComponent
    }

    fun hostFacade(): HostGameFacade {
        checkNotNull(ongoingGameComponent) { "No on-going game component!" }
        return ongoingGameComponent!!.hostGameFacade
    }

    fun guestFacade(): GuestGameFacade {
        checkNotNull(ongoingGameComponent) { "No on-going game component!" }
        return ongoingGameComponent!!.guestGameFacade
    }

    open fun homeFacade(): HomeFacade {
        return gameComponent.homeFacade
    }

    fun getGameUiComponent(): OngoingGameUiComponent? {
        checkNotNull(ongoingGameComponent) { "No on-going game ui component!" }
        return ongoingGameUiComponent
    }

    fun stopOngoingGame() {
        ongoingGameUiComponent = null
        ongoingGameComponent = null
        inGameStoreComponent = null
    }

    private fun createNotificationChannels() {
        NotificationChannelFactory.createDefaultNotificationChannel(this)
    }
}
