package ch.qscqlmpa.dwitch.ingame.services

import android.content.Context
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.app.ServiceIdentifier
import ch.qscqlmpa.dwitchgame.gamelifecycle.*
import org.tinylog.Logger
import javax.inject.Inject

class ServiceManagerImpl @Inject constructor(
    private val context: Context,
    appEventRepository: AppEventRepository,
    gameLifecycleFacade: GameLifecycleFacade
) : ServiceManager {

    private var runningService: ServiceIdentifier? = null

    init {
        // Observables have the lifecycle of the application
        appEventRepository.observeEvents().subscribe(
            { event ->
                when (event) {
                    is AppEvent.ServiceStarted -> runningService = event.serviceIdentifier
                }
            },
            { error -> Logger.error(error) { "Error while observing app events." } }
        )

        gameLifecycleFacade.observeHostEvents().subscribe(
            { event ->
                when (event) {
                    is HostGameLifecycleEvent.GameSetup -> startHostService(event.gameInfo)
                    HostGameLifecycleEvent.MovedToGameRoom -> goToHostGameRoom()
                    else -> {
                        // Nothing to do
                    }
                }
            },
            { error -> Logger.error(error) { "Error while observing host events." } }
        )
        gameLifecycleFacade.observeGuestEvents().subscribe(
            { event ->
                when (event) {
                    is GuestGameLifecycleEvent.GameSetup -> startGuestService(event.gameInfo)
                    GuestGameLifecycleEvent.MovedToGameRoom -> goToGuestGameRoom()
                    else -> {
                        // Nothing to do
                    }
                }
            },
            { error -> Logger.error(error) { "Error while observing guest events." } }
        )
    }

    override fun stop() {
        when (runningService) {
            ServiceIdentifier.Guest -> stopGuestService()
            ServiceIdentifier.Host -> stopHostService()
            else -> {
                Logger.warn { "stop() has been called when neither the guest nor the host service is running." }
            }
        }
        runningService = null
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
