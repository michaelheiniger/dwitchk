package ch.qscqlmpa.dwitch.app

import ch.qscqlmpa.dwitch.DaggerTestAppComponent
import ch.qscqlmpa.dwitch.TestAppComponent
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameComponent
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameModule
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
            ongoingGameComponent = testAppComponent.addInGameComponent(
                OngoingGameModule(playerRole, roomType, gameLocalId,
                    localPlayerLocalId, hostPort, hostIpAddress)
            )
        } else {
            Timber.w("startOngoingGame() called when a game is already on-going.")
        }
        return ongoingGameComponent
    }

}
