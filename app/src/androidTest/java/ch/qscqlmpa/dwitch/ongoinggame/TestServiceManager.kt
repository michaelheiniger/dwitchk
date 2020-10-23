package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import javax.inject.Inject

class TestServiceManager @Inject constructor() : ServiceManager {

    override fun startHostService(gameLocalId: Long, localPlayerLocalId: Long) {
        // Nothing to do
    }

    override fun startGuestService(gameLocalId: Long, localPlayerLocalId: Long, hostPort: Int, hostIpAddress: String) {
        // Nothing to do
    }

    override fun stopHostService() {
        // Nothing to do
    }

    override fun goToHostGameRoom() {
        // Nothing to do
    }
}