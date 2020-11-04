package ch.qscqlmpa.dwitch.ongoinggame.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.common.CommonExtraConstants
import ch.qscqlmpa.dwitch.common.NotificationChannelConstants
import ch.qscqlmpa.dwitch.gameadvertising.GameInfo
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameComponent
import ch.qscqlmpa.dwitch.service.BaseService
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.GameRoomActivity
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomActivity
import timber.log.Timber

abstract class BaseInGameService : BaseService() {

    protected abstract val playerRole: PlayerRole

    protected abstract fun actionStartService(intent: Intent)

    protected abstract fun actionChangeRoomToGameRoom()

    protected abstract fun cleanUp()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_START_SERVICE -> actionStartService(intent)
            ACTION_CHANGE_ROOM_TO_GAME_ROOM -> actionChangeRoomToGameRoom()
            ACTION_STOP_SERVICE -> actionStopService()
        }
        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUp()
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not implemented because not needed")
    }

    protected fun getOngoingGameComponent(): OngoingGameComponent {
        return (application as App).getGameComponent()!!
    }

    protected fun getGameLocalId(intent: Intent): Long {
        val gameLocalId = intent.getLongExtra(EXTRA_GAME_LOCAL_ID, 0)

        if (gameLocalId == 0L) {
            throw IllegalArgumentException("The intent to start the service does not specify a game local-ID")
        }
        return gameLocalId
    }

    protected fun getLocalPlayerLocalId(intent: Intent): Long {
        val localPlayerLocalId =
            intent.getLongExtra(EXTRA_LOCAL_PLAYER_LOCAL_ID, 0)

        if (localPlayerLocalId == 0L) {
            throw IllegalArgumentException("The intent to start the service does not specify a local player local-ID")
        }
        return localPlayerLocalId
    }

    protected fun getGameInfo(intent: Intent): GameInfo {
        return intent.getParcelableExtra(EXTRA_GAME_INFO)
    }

    protected fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationChannelConstants.DEFAULT_CHANNEL_ID,
            NotificationChannelConstants.DEFAULT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
    }

    protected fun showNotification(roomType: RoomType) {
        val notificationIntent = buildNotificationIntent(roomType)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(
            this,
            NotificationChannelConstants.DEFAULT_CHANNEL_ID
        )
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSmallIcon(R.drawable.spades_ace)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(getColor(R.color.black))

        if (BuildConfig.DEBUG) {
            notificationBuilder.addAction(addKillServiceButtonToNotif())
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun buildNotificationIntent(roomType: RoomType): Intent {
        val notificationIntent = when (roomType) {
            RoomType.WAITING_ROOM -> Intent(this, WaitingRoomActivity::class.java)
            RoomType.GAME_ROOM -> Intent(this, GameRoomActivity::class.java)
        }
        notificationIntent.putExtra(CommonExtraConstants.EXTRA_PLAYER_ROLE, playerRole.name)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return notificationIntent
    }

    private fun addKillServiceButtonToNotif(): NotificationCompat.Action {
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            createIntent(this, this::class.java, ACTION_STOP_SERVICE),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(
            R.drawable.ic_stop_black_24dp,
            getString(R.string.stop_game_service),
            stopPendingIntent
        )
    }

    private fun actionStopService() {
        Timber.i("Stop service")
        stopSelf()
        cleanUp()
    }

    companion object {
        private const val NOTIFICATION_ID = 1

        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_CHANGE_ROOM_TO_GAME_ROOM = "ACTION_GO_TO_GAME_ROOM"

        const val EXTRA_GAME_LOCAL_ID = "game_local_id"
        const val EXTRA_GAME_INFO = "game_info"
        const val EXTRA_LOCAL_PLAYER_LOCAL_ID = "local_player_local_id"

        @JvmStatic
        protected fun createIntent(context: Context, cls: Class<*>, action: String): Intent {
            val intent = Intent(context, cls)
            intent.action = action
            return intent
        }
    }

}