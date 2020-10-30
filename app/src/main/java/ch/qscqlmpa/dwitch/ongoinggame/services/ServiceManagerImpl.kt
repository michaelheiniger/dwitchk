package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import ch.qscqlmpa.dwitch.gameadvertising.GameInfo
import javax.inject.Inject

open class ServiceManagerImpl @Inject constructor(private val context: Context) : ServiceManager {

    override fun startHostService(gameLocalId: Long, gameInfo: GameInfo, localPlayerLocalId: Long) {
        HostInGameService.startService(context, gameLocalId, gameInfo, localPlayerLocalId)
    }

    override fun stopHostService() {
        HostInGameService.stopService(context)
    }

    override fun startGuestService(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        gamePort: Int,
        gameIpAddress: String
    ) {
        GuestInGameService.startService(
            context,
            gameLocalId,
            localPlayerLocalId,
            gamePort,
            gameIpAddress
        )
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