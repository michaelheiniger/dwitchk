package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameModule
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

open class App : DaggerApplication() {

    private lateinit var appComponent: AppComponent
    protected var ongoingGameComponent: OngoingGameComponent? = null

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .applicationModule(ApplicationModule(this))
                .build()
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

//        if (BuildConfig.DEBUG) {//FIXME
            Timber.plant(Timber.DebugTree())
//        }
    }

    open fun startOngoingGame(playerRole: PlayerRole,
                              roomType: RoomType,
                              gameLocalId: Long,
                              localPlayerLocalId: Long,
                              hostPort: Int,
                              hostIpAddress: String
    ): OngoingGameComponent? {
        Timber.d("startOngoingGame()")
        if (ongoingGameComponent == null) {
            ongoingGameComponent = appComponent.addInGameComponent(
                OngoingGameModule(playerRole, roomType, gameLocalId,
                    localPlayerLocalId, hostPort, hostIpAddress)
            )
        } else {
            Timber.w("startOngoingGame() called when a game is already on-going.")
        }
        return ongoingGameComponent
    }

    fun getGameComponent(): OngoingGameComponent? {
        checkNotNull(ongoingGameComponent) { "No on-going game !" }
        return ongoingGameComponent
    }

    fun stopOngoingGame() {
        ongoingGameComponent = null
    }
}
