package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.ServiceManager
import javax.inject.Inject

class TestServiceManager @Inject constructor() : ServiceManager {

    override fun startHostService(gameLocalId: Long, localPlayerLocalId: Long) {
        TODO("Not yet implemented")
    }

    override fun startGuestService(gameLocalId: Long, localPlayerLocalId: Long, hostPort: Int, hostIpAddress: String) {
        TODO("Not yet implemented")
    }

    override fun stopHostService() {
        TODO("Not yet implemented")
    }

    override fun goToHostGameRoom() {
        TODO("Not yet implemented")
    }
}