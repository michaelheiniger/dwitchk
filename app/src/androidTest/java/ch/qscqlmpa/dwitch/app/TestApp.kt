package ch.qscqlmpa.dwitch.base

import ch.qscqlmpa.dwitch.App
import ch.qscqlmpa.dwitch.components.OngoingGameComponent
import ch.qscqlmpa.dwitch.components.ongoinggame.OngoingGameModule
import ch.qscqlmpa.dwitch.di.ApplicationModule
import ch.qscqlmpa.dwitch.di.DaggerTestAppComponent
import ch.qscqlmpa.dwitch.di.TestAppComponent
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

class TestApp : App() {

    lateinit var testAppComponent: TestAppComponent

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        testAppComponent = DaggerTestAppComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        return testAppComponent
    }

    override fun startOngoingGame(playerRole: PlayerRole,
                                  roomType: RoomType,
                                  gameLocalId: Long,
                                  localPlayerLocalId: Long,
                                  hostPort: Int,
                                  hostIpAddress: String
    ): OngoingGameComponent? {
        if (ongoingGameComponent == null) {
            ongoingGameComponent = testAppComponent.addInGameComponent(OngoingGameModule(playerRole, roomType, gameLocalId,
                    localPlayerLocalId, hostPort, hostIpAddress))
        } else {
            Timber.w("startOngoingGame() called when a game is already on-going.")
        }
        return ongoingGameComponent
    }

}
