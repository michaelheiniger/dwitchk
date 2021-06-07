package ch.qscqlmpa.dwitch.ongoinggame.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.HomeActivity
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.app.notifications.NotificationChannelFactory.DEFAULT_CHANNEL_ID
import ch.qscqlmpa.dwitch.common.CommonExtraConstants
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import dagger.android.DaggerService
import org.tinylog.kotlin.Logger
import javax.inject.Inject

abstract class BaseInGameService : DaggerService() {

    @Inject
    lateinit var appEventRepository: AppEventRepository

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
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanUp()
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not implemented because not needed")
    }

    protected val app: App by lazy { application as App }

    protected fun showNotification(roomType: RoomType) {
        val notificationIntent = buildNotificationIntent()

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationMessage = when (roomType) {
            RoomType.WAITING_ROOM -> R.string.waitingroom_notification_message
            RoomType.GAME_ROOM -> R.string.gameroom_notification_message
        }

        val notificationBuilder = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSmallIcon(R.drawable.spades_ace)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(notificationMessage))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(getColor(R.color.black))

        if (BuildConfig.DEBUG) {
            notificationBuilder.addAction(addKillServiceButtonToNotif())
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    protected fun notifyServiceStarted() {
        appEventRepository.notify(AppEvent.ServiceStarted)
    }

    private fun buildNotificationIntent(): Intent {
        val notificationIntent = Intent(this, HomeActivity::class.java)
        notificationIntent.putExtra(CommonExtraConstants.EXTRA_PLAYER_ROLE, playerRole.name)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return notificationIntent
    }

    private fun addKillServiceButtonToNotif(): NotificationCompat.Action {
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            createIntent(this, this::class.java, ACTION_STOP_SERVICE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Action(
            R.drawable.ic_stop_black_24dp,
            getString(R.string.stop_game_service),
            stopPendingIntent
        )
    }

    private fun actionStopService() {
        Logger.info { "Stop service" }
        stopSelf()
        cleanUp()
    }

    companion object {
        private const val NOTIFICATION_ID = 1

        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
        const val ACTION_CHANGE_ROOM_TO_GAME_ROOM = "ACTION_GO_TO_GAME_ROOM"

        @JvmStatic
        protected fun createIntent(context: Context, cls: Class<*>, action: String): Intent {
            val intent = Intent(context, cls)
            intent.action = action
            return intent
        }
    }
}
