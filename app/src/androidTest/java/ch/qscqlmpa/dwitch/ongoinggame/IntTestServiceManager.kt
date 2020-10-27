package ch.qscqlmpa.dwitch.ongoinggame

import ch.qscqlmpa.dwitch.IntTestAppComponent
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import javax.inject.Inject

class IntTestServiceManager @Inject constructor() : ServiceManager {

    private lateinit var appComponent: IntTestAppComponent
    private lateinit var onGoingGameComponent: IntTestOngoingGameComponent

    fun setAppComponent(appComponent: IntTestAppComponent) {
        this.appComponent = appComponent
    }

    fun getOnGoingGameComponent(): IntTestOngoingGameComponent {
        return onGoingGameComponent
    }

    override fun startHostService(gameLocalId: Long, localPlayerLocalId: Long) {
        onGoingGameComponent = appComponent.addInGameComponent(
            OngoingGameModule(
                PlayerRole.HOST,
                RoomType.WAITING_ROOM,
                gameLocalId,
                localPlayerLocalId,
                8889,
                "127.0.0.1"
            )
        )
        onGoingGameComponent.hostCommunicator.listenForConnections()
    }

    override fun stopHostService() {
        onGoingGameComponent.hostCommunicator.closeAllConnections()
    }

    override fun goToHostGameRoom() {
        // Nothing to do
    }

    override fun startGuestService(
        gameLocalId: Long,
        localPlayerLocalId: Long,
        hostPort: Int,
        hostIpAddress: String
    ) {
        onGoingGameComponent = appComponent.addInGameComponent(
            OngoingGameModule(
                PlayerRole.GUEST,
                RoomType.WAITING_ROOM,
                gameLocalId,
                localPlayerLocalId,
                hostPort,
                hostIpAddress
            )
        )
        // Cannot be called before hooking up the Guest with the Host
        //onGoingGameComponent.guestCommunicator.connect()
    }

    override fun stopGuestService() {
        onGoingGameComponent.guestCommunicator.closeConnection()
    }

    override fun goToGuestGameRoom() {
        // Nothing to do
    }
}