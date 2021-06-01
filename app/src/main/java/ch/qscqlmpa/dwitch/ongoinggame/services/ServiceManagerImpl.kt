package ch.qscqlmpa.dwitch.ongoinggame.services

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
                HostGameLifecycleEvent.GameCanceled, HostGameLifecycleEvent.GameOver -> stopHostService()
                HostGameLifecycleEvent.MovedToGameRoom -> goToHostGameRoom()
            }
        }
        homeFacade.observeGuestGameEvents().subscribe { event ->
            when (event) {
                is GuestGameLifecycleEvent.GameJoined -> startGuestService(event.gameInfo)
                GuestGameLifecycleEvent.KickedOffGame,
                GuestGameLifecycleEvent.GuestLeftGame,
                GuestGameLifecycleEvent.GameCanceled,
                GuestGameLifecycleEvent.GameOver -> stopGuestService()
                GuestGameLifecycleEvent.MovedToGameRoom -> goToGuestGameRoom()
            }
        }
    }

    private fun startHostService(gameCreatedInfo: GameCreatedInfo) {
        HostInGameService.startService(context, gameCreatedInfo)
    }

    private fun stopHostService() {
        HostInGameService.stopService(context)
    }

    private fun startGuestService(gameJoinedInfo: GameJoinedInfo) {
        GuestInGameService.startService(context, gameJoinedInfo)
    }

    private fun stopGuestService() {
        GuestInGameService.stopService(context)
    }

    private fun goToHostGameRoom() {
        HostInGameService.changeRoomToGameRoom(context)
    }

    private fun goToGuestGameRoom() {
        GuestInGameService.goChangeRoomToGameRoom(context)
    }
}
