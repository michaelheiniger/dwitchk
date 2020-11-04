package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import timber.log.Timber


class GuestInGameService : BaseInGameService() {

    override val playerRole = PlayerRole.GUEST

    override fun actionStartService(intent: Intent) {
        val gameLocalId = getGameLocalId(intent)
        val localPlayerLocalId = getLocalPlayerLocalId(intent)
        val hostIpAddress = getHostIpAddress(intent)
        val hostPort = getHostPort(intent)

        Timber.i("Start service")
        showNotification(RoomType.WAITING_ROOM)
        (application as App).startOngoingGame(
            playerRole,
            RoomType.WAITING_ROOM,
            gameLocalId,
            localPlayerLocalId,
            hostPort,
            hostIpAddress
        )
        getOngoingGameComponent().guestCommunicator.connect()
    }

    override fun actionChangeRoomToGameRoom() {
        Timber.i("Go to game room")
        showNotification(RoomType.GAME_ROOM)
    }

    override fun cleanUp() {
        getOngoingGameComponent().guestCommunicator.closeConnection()
        (application as App).stopOngoingGame()
    }

    private fun getHostIpAddress(intent: Intent): String {
        return intent.getStringExtra(EXTRA_HOST_IP_ADDRESS)
            ?: throw IllegalArgumentException(
                "The intent to start the service does not specify a host ip address"
            )
    }

    private fun getHostPort(intent: Intent): Int {
        val hostPort = intent.getIntExtra(EXTRA_HOST_PORT, 0)

        if (hostPort == 0) {
            throw IllegalArgumentException("The intent to start the service does not specify a host port")
        }
        return hostPort
    }

    companion object {

        private const val EXTRA_HOST_PORT = "host_port"
        private const val EXTRA_HOST_IP_ADDRESS = "host_ip_address"

        fun startService(
            context: Context,
            gameLocalId: Long,
            localPlayerLocalId: Long,
            hostPort: Int,
            hostIpAddress: String
        ) {
            Timber.i("Starting GuestService...()")
            val intent = Intent(context, GuestInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_LOCAL_ID, gameLocalId)
            intent.putExtra(EXTRA_LOCAL_PLAYER_LOCAL_ID, localPlayerLocalId)
            intent.putExtra(EXTRA_HOST_IP_ADDRESS, hostIpAddress)
            intent.putExtra(EXTRA_HOST_PORT, hostPort)
            context.startService(intent)
        }

        fun goChangeRoomToGameRoom(context: Context) {
            Timber.i("Changing room to Game Room...")
            val intent = createIntent(context, GuestInGameService::class.java, ACTION_CHANGE_ROOM_TO_GAME_ROOM)
            context.startService(intent)
        }

        fun stopService(context: Context) {
            Timber.i("Stopping GuestService...()")
            val intent = Intent(context, GuestInGameService::class.java)
            context.stopService(intent)
        }
    }
}
