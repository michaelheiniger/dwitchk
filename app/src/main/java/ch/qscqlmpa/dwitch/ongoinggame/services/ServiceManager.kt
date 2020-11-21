package ch.qscqlmpa.dwitch.ongoinggame.services

import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo

interface ServiceManager {

    fun startHostService(gameCreatedInfo: GameCreatedInfo)

    fun stopHostService()

    fun startGuestService(gameJoinedInfo: GameJoinedInfo)

    fun stopGuestService()

    fun goToHostGameRoom()

    fun goToGuestGameRoom()
}