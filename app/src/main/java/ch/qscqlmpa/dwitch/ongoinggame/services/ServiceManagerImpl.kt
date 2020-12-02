package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo
import javax.inject.Inject

open class ServiceManagerImpl @Inject constructor(private val context: Context) : ServiceManager {

    override fun startHostService(gameCreatedInfo: GameCreatedInfo) {
        HostInGameService.startService(context, gameCreatedInfo)
    }

    override fun stopHostService() {
        HostInGameService.stopService(context)
    }

    override fun startGuestService(gameJoinedInfo: GameJoinedInfo) {
        GuestInGameService.startService(context, gameJoinedInfo)
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