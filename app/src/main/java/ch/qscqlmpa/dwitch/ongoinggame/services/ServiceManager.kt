package ch.qscqlmpa.dwitch.ongoinggame.services

import ch.qscqlmpa.dwitch.gameadvertising.GameInfo

interface ServiceManager {

    fun startHostService(gameLocalId: Long, gameInfo: GameInfo, localPlayerLocalId: Long)

    fun stopHostService()

    fun startGuestService(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        gamePort: Int,
        gameIpAddress: String
    )

    fun stopGuestService()

    fun goToHostGameRoom()

    fun goToGuestGameRoom()
}