package ch.qscqlmpa.dwitch.ongoinggame.services

interface ServiceManager {

    fun startHostService(gameLocalId: Long, localPlayerLocalId: Long)

    fun stopHostService()

    fun startGuestService(gameLocalId: Long, localPlayerLocalId: Long, hostPort: Int, hostIpAddress: String)

    fun stopGuestService()

    fun goToHostGameRoom()
}