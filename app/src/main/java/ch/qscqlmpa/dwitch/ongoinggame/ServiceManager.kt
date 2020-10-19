package ch.qscqlmpa.dwitch.ongoinggame

interface ServiceManager {

    fun startHostService(gameLocalId: Long, localPlayerLocalId: Long)

    fun startGuestService(gameLocalId: Long, localPlayerLocalId: Long, hostPort: Int, hostIpAddress: String)

    fun stopHostService()

    fun goToHostGameRoom()
}