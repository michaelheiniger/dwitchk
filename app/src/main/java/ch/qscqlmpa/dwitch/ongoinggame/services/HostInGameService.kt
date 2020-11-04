package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitch.gameadvertising.GameInfo
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.utils.DisposableManager
import timber.log.Timber


class HostInGameService : BaseInGameService() {

    private val gameAdvertisingDisposable = DisposableManager()

    override val playerRole = PlayerRole.HOST

    override fun actionStartService(intent: Intent) {
        val gameLocalId = getGameLocalId(intent)
        val gameInfo = getGameInfo(intent)
        val localPlayerLocalId = getLocalPlayerLocalId(intent)

        Timber.i("Start service")
        createNotificationChannel()
        showNotification(RoomType.WAITING_ROOM)
        (application as App).startOngoingGame(
            playerRole,
            RoomType.WAITING_ROOM,
            gameLocalId,
            localPlayerLocalId,
            gameInfo.gamePort,
            "0.0.0.0"
        )
        getOngoingGameComponent().hostCommunicator.listenForConnections()
        advertiseGame(gameInfo)
    }

    override fun actionChangeRoomToGameRoom() {
        Timber.i("Go to game room")
        showNotification(RoomType.GAME_ROOM)
        gameAdvertisingDisposable.dispose()
    }

    override fun cleanUp() {
        getOngoingGameComponent().hostCommunicator.closeAllConnections()
        (application as App).stopOngoingGame()
        gameAdvertisingDisposable.dispose()
    }

    private fun advertiseGame(gameInfo: GameInfo) {
        gameAdvertisingDisposable.add(
            getOngoingGameComponent().gameAdvertising.startAdvertising(gameInfo).subscribe()
        )
    }

    companion object {

        fun startService(
            context: Context,
            gameLocalId: Long,
            gameInfo: GameInfo,
            localPlayerLocalId: Long
        ) {
            Timber.i("Starting HostService...()")
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_LOCAL_ID, gameLocalId)
            intent.putExtra(EXTRA_GAME_INFO, gameInfo)
            intent.putExtra(EXTRA_LOCAL_PLAYER_LOCAL_ID, localPlayerLocalId)
            context.startService(intent)
        }

        fun changeRoomToGameRoom(context: Context) {
            Timber.i("Changing room to Game Room...")
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_CHANGE_ROOM_TO_GAME_ROOM
            context.startService(intent)
        }

        fun stopService(context: Context) {
            Timber.i("Stopping HostService...")
            context.stopService(Intent(context, HostInGameService::class.java))
        }
    }
}
