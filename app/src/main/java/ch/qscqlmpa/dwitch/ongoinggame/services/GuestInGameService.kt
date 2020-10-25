package ch.qscqlmpa.dwitch.ongoinggame.services

import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_GAME_LOCAL_ID
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_LOCAL_PLAYER_LOCAL_ID
import ch.qscqlmpa.dwitch.common.CommonExtraConstants.EXTRA_PLAYER_ROLE
import ch.qscqlmpa.dwitch.common.NotificationChannelConstants.DEFAULT_CHANNEL_ID
import ch.qscqlmpa.dwitch.common.NotificationChannelConstants.DEFAULT_CHANNEL_NAME
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameComponent
import ch.qscqlmpa.dwitch.persistence.Store
import ch.qscqlmpa.dwitch.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import timber.log.Timber
import javax.inject.Inject


class GuestInGameService : BaseInGameService() {

    @Inject
    lateinit var store: Store

    @Inject
    lateinit var schedulerFactory: SchedulerFactory

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_START_SERVICE -> actionStartService(intent)
            ACTION_GO_TO_GAME_ROOM -> actionGoToGameRoom()
            ACTION_STOP_SERVICE -> actionStopService()
        }
        return Service.START_REDELIVER_INTENT
    }

    private fun getOngoingGameComponent(): OngoingGameComponent {
        return (application as App).getGameComponent()!!
    }

    private fun actionStartService(intent: Intent) {
        val gameLocalId = getGameLocalId(intent)
        val localPlayerLocalId = getLocalPlayerLocalId(intent)
        val hostIpAddress = getHostIpAddress(intent)
        val hostPort = getHostPort(intent)

        Timber.i("Start service")
        showNotification(RoomType.WAITING_ROOM)
        (application as App).startOngoingGame(
                PlayerRole.GUEST,
                RoomType.WAITING_ROOM,
                gameLocalId,
                localPlayerLocalId,
                hostPort,
                hostIpAddress
        )
        getOngoingGameComponent().guestCommunicator.connect()
    }

    private fun actionGoToGameRoom() {
        Timber.i("Go to game room")
        showNotification(RoomType.GAME_ROOM)
    }

    private fun actionStopService() {
        Timber.i("Stop service")
        stopSelf()
        getOngoingGameComponent().guestCommunicator.closeConnection()
        (application as App).stopOngoingGame()
    }

    private fun getHostIpAddress(intent: Intent): String {
        return intent.getStringExtra(EXTRA_HOST_IP_ADDRESS)
                ?: throw IllegalArgumentException("The intent to start the service does not specify a host ip address")
    }

    private fun getHostPort(intent: Intent): Int {
        val hostPort = intent.getIntExtra(EXTRA_HOST_PORT, 0)

        if (hostPort == 0) {
            throw IllegalArgumentException("The intent to start the service does not specify a host port")
        }
        return hostPort
    }

    private fun showNotification(roomType: RoomType) {

        createNotificationChannel(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME)

        val notificationIntent: Intent
        when (roomType) {
            RoomType.WAITING_ROOM -> notificationIntent = Intent(this, WaitingRoomActivity::class.java)
            RoomType.GAME_ROOM -> throw NotImplementedError() //TODO
        }
        notificationIntent.putExtra(EXTRA_PLAYER_ROLE, PlayerRole.HOST.name)

        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.spades_ace)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(getColor(R.color.black))


        // Add "Stop" button to kill service. Only in DEBUG build.
//        if (BuildConfig.DEBUG) {//FIXME
            val stopIntent = Intent(this, GuestInGameService::class.java)
            stopIntent.action = ACTION_STOP_SERVICE
            val stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, FLAG_UPDATE_CURRENT)

            notificationBuilder.addAction(R.drawable.ic_stop_black_24dp, getString(R.string.stop_game_service),
                    stopPendingIntent)
//        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    companion object {

        private const val EXTRA_HOST_PORT = "host_port"
        private const val EXTRA_HOST_IP_ADDRESS = "host_ip_address"

        fun startService(context: Context, gameLocalId: Long, localPlayerLocalId: Long, hostPort: Int, hostIpAddress: String) {
            Timber.i("Starting GuestService...()")
            val intent = Intent(context, GuestInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_LOCAL_ID, gameLocalId)
            intent.putExtra(EXTRA_LOCAL_PLAYER_LOCAL_ID, localPlayerLocalId)
            intent.putExtra(EXTRA_HOST_IP_ADDRESS, hostIpAddress)
            intent.putExtra(EXTRA_HOST_PORT, hostPort)
            context.startService(intent)
        }

        fun goToGameRoom(context: Context) {
            Timber.i("Changing room to Game Room...")
            val intent = Intent(context, GuestInGameService::class.java)
            intent.action = ACTION_GO_TO_GAME_ROOM
            context.startService(intent)
        }

        fun stopService(context: Context) {
            Timber.i("Stopping GuestService...()")
            val intent = Intent(context, GuestInGameService::class.java)
            intent.action = ACTION_STOP_SERVICE
            context.stopService(intent)
        }

        private const val NOTIFICATION_ID = 1
        private const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        private const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        private const val ACTION_GO_TO_GAME_ROOM = "ACTION_GO_TO_GAME_ROOM"
    }
}
