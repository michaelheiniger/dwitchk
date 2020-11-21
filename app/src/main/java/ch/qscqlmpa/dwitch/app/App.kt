package ch.qscqlmpa.dwitch.app

import android.annotation.SuppressLint
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory
import ch.qscqlmpa.dwitch.ongoinggame.OnGoingGameUiModule
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameUiComponent
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitchcommunication.di.CommunicationComponent
import ch.qscqlmpa.dwitchcommunication.di.CommunicationModule
import ch.qscqlmpa.dwitchcommunication.di.DaggerCommunicationComponent
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.di.DaggerGameComponent
import ch.qscqlmpa.dwitchgame.di.GameComponent
import ch.qscqlmpa.dwitchgame.di.modules.GameModule
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
import timber.log.Timber
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
        storeComponent = DaggerStoreComponent.factory()
            .create(StoreModule(this))

        gameComponent = DaggerGameComponent.factory()
            .create(GameModule(storeComponent.store))

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .applicationModule(ApplicationModule(this))
            .gameComponent(gameComponent)
            .build()
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createNotificationChannels()
        observeAppEvents()
    }

    open fun startOngoingGame(
        playerRole: PlayerRole,
        roomType: RoomType,
        gameLocalId: Long,
        localPlayerLocalId: Long,
        hostPort: Int,
        hostIpAddress: String
    ): OngoingGameComponent? {
        Timber.d("startOngoingGame()")
        if (ongoingGameComponent == null) {
            inGameStoreComponent = storeComponent.addInGameStoreComponent(
                InGameStoreModule(gameLocalId, localPlayerLocalId)
            )

            communicationComponent = DaggerCommunicationComponent.factory()
                .create(CommunicationModule(hostIpAddress, hostPort))

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
                ),
            )
            ongoingGameUiComponent = appComponent.addOngoingGameUiComponent(
                OnGoingGameUiModule(
                    ongoingGameComponent!!.hostFacade,
                    ongoingGameComponent!!.guestFacade,
                    ongoingGameComponent!!.waitingRoomFacade,
                    ongoingGameComponent!!.waitingRoomHostFacade,
                    ongoingGameComponent!!.waitingRoomGuestFacade,
                    ongoingGameComponent!!.gameRoomHostFacade,
                    ongoingGameComponent!!.gameRoomGuestFacade,
                    ongoingGameComponent!!.playerDashboardFacade
                )
            )
        } else {
            Timber.w("startOngoingGame() called when a game is already on-going.")
        }
        return ongoingGameComponent
    }

    fun getGameComponent(): OngoingGameComponent? {
        checkNotNull(ongoingGameComponent) { "No on-going game component!" }
        return ongoingGameComponent
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

    protected open fun appEventRepository(): AppEventRepository {
        return gameComponent.appEventRepository
    }

    //FIXME: Le crash vient d'ici !
    @SuppressLint("CheckResult") // Subscription is disposed when app is destroyed
    protected open fun observeAppEvents() {
        appEventRepository().observeEvents()
            .subscribe { event ->
                when (event) {
                    is AppEvent.GameCreated -> serviceManager.startHostService(event.gameInfo)
                    is AppEvent.GameJoined -> serviceManager.startGuestService(event.gameInfo)
                    AppEvent.GameRoomJoinedByHost -> serviceManager.goToHostGameRoom()
                    AppEvent.GameRoomJoinedByGuest -> serviceManager.goToGuestGameRoom()
                    AppEvent.GameOver -> serviceManager.stopHostService()
                    AppEvent.GameLeft -> serviceManager.stopGuestService()
                }
            }
    }
}
