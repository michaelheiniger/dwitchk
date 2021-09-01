package ch.qscqlmpa.dwitch.ingame.services

import android.content.Context
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import javax.inject.Inject

class ServiceManagerImpl @Inject constructor(
    private val context: Context,
    homeFacade: HomeFacade
) : ServiceManager {

    private var gameOverHost: Boolean = false
    private var gameOverGuest: Boolean = false

    init {
        // Observables have the lifecycle of the application
        homeFacade.observeHostGameEvents().subscribe { event ->
            when (event) {
                is HostGameLifecycleEvent.GameSetup -> startHostService(event.gameInfo)
                HostGameLifecycleEvent.MovedToGameRoom -> goToHostGameRoom()
                HostGameLifecycleEvent.GameOver -> gameOverHost = true
            }
        }
        homeFacade.observeGuestGameEvents().subscribe { event ->
            when (event) {
                is GuestGameLifecycleEvent.GameSetup -> startGuestService(event.gameInfo)
                GuestGameLifecycleEvent.MovedToGameRoom -> goToGuestGameRoom()
                GuestGameLifecycleEvent.GameOver -> gameOverGuest = true
            }
        }
    }

    override fun stop() {
        if (gameOverHost) {
            gameOverHost = false
            stopHostService()
        }
        if (gameOverGuest) {
            gameOverGuest = false
            stopGuestService()
        }
    }

    private fun stopHostService() {
        HostInGameService.stopService(context)
    }

    private fun stopGuestService() {
        GuestInGameService.stopService(context)
    }

    private fun startHostService(gameCreatedInfo: GameCreatedInfo) {
        HostInGameService.startService(context, gameCreatedInfo)
    }

    private fun startGuestService(gameJoinedInfo: GameJoinedInfo) {
        GuestInGameService.startService(context, gameJoinedInfo)
    }

    private fun goToHostGameRoom() {
        HostInGameService.changeRoomToGameRoom(context)
    }

    private fun goToGuestGameRoom() {
        GuestInGameService.goChangeRoomToGameRoom(context)
    }
}
