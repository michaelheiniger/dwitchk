package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import javax.inject.Inject

open class ServiceManagerImpl @Inject constructor(private val context: Context) : ServiceManager {

    override fun startHostService(gameLocalId: Long, localPlayerLocalId: Long) {
        HostInGameService.startService(context, gameLocalId, localPlayerLocalId)
    }

    override fun stopHostService() {
        HostInGameService.stopService(context)
    }

    override fun startGuestService(gameLocalId: Long, localPlayerLocalId: Long, hostPort: Int, hostIpAddress: String) {
        GuestInGameService.startService(context, gameLocalId, localPlayerLocalId, hostPort, hostIpAddress)
    }

    override fun stopGuestService() {
        GuestInGameService.stopService(context)
    }

    override fun goToHostGameRoom() {
        HostInGameService.changeRoomToGameRoom(context)
    }

    override fun goToGuestGameRoom() {
        GuestInGameService.goChangeRoomToGameRoom(context)
    }
}