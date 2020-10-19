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
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import timber.log.Timber


class HostInGameService : BaseInGameService() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_START_SERVICE -> actionStartService(intent)
            ACTION_GO_TO_GAME_ROOM -> actionGoToGameRoom()
            ACTION_STOP_SERVICE -> actionStopService()
        }

        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        getOngoingGameComponent().hostCommunication().closeAllConnections()
    }

    private fun getOngoingGameComponent(): OngoingGameComponent {
        return (application as App).getGameComponent()!!
    }

    private fun actionStartService(intent: Intent) {
        val gameLocalId = getGameLocalId(intent)
        val localPlayerLocalId = getLocalPlayerLocalId(intent)

        Timber.i("Start service")
        showNotification(RoomType.WAITING_ROOM)
        (application as App).startOngoingGame(
                PlayerRole.HOST,
                RoomType.WAITING_ROOM,
                gameLocalId,
                localPlayerLocalId,
                8889,
                "127.0.0.1"
        ) //TODO
        // extract host port from settings
    }

    private fun actionGoToGameRoom() {
        Timber.i("Go to game room")
        showNotification(RoomType.GAME_ROOM)
    }

    private fun actionStopService() {
        Timber.i("Stop service")
        stopSelf()
        (application as App).stopOngoingGame()
    }

    private fun showNotification(roomType: RoomType) {
        createNotificationChannel(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME)

        val notificationIntent: Intent = when (roomType) {
            RoomType.WAITING_ROOM -> Intent(this, WaitingRoomActivity::class.java)
            RoomType.GAME_ROOM -> Intent(this, GameRoomActivity::class.java)
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
//        if (BuildConfig.DEBUG) { //FIXME
            val stopIntent = Intent(this, HostInGameService::class.java)
            stopIntent.action = ACTION_STOP_SERVICE
            val stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, FLAG_UPDATE_CURRENT)

            notificationBuilder.addAction(R.drawable.ic_stop_black_24dp, getString(R.string.stop_game_service), stopPendingIntent)
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

    override fun onBind(intent: Intent): IBinder {
        throw NotImplementedError()
    }

    companion object {

        fun startService(context: Context, gameLocalId: Long, localPlayerLocalId: Long) {
            Timber.i("Starting HostService...()")
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_LOCAL_ID, gameLocalId)
            intent.putExtra(EXTRA_LOCAL_PLAYER_LOCAL_ID, localPlayerLocalId)
            context.startService(intent)
        }

        fun goToGameRoom(context: Context) {
            Timber.i("Changing room to Game Room...")
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_GO_TO_GAME_ROOM
            context.startService(intent)
        }

        fun stopService(context: Context) {
            Timber.i("Stopping HostService...")
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_STOP_SERVICE
            context.stopService(intent)
        }

        private const val NOTIFICATION_ID = 1
        private const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        private const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        private const val ACTION_GO_TO_GAME_ROOM = "ACTION_GO_TO_GAME_ROOM"
    }
}
