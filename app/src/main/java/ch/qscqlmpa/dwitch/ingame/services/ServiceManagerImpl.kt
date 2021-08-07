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
    private val homeFacade: HomeFacade
) : ServiceManager {

    override fun init() {
        // Observables have the lifecycle of the application
        homeFacade.observeHostGameEvents().subscribe { event ->
            when (event) {
                is HostGameLifecycleEvent.GameCreated -> startHostService(event.gameInfo)
                HostGameLifecycleEvent.MovedToGameRoom -> goToHostGameRoom()
            }
        }
        homeFacade.observeGuestGameEvents().subscribe { event ->
            when (event) {
                is GuestGameLifecycleEvent.GameJoined -> startGuestService(event.gameInfo)
                GuestGameLifecycleEvent.MovedToGameRoom -> goToGuestGameRoom()
            }
        }
    }

    override fun stopHostService() {
        HostInGameService.stopService(context)
    }

    override fun stopGuestService() {
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
