package ch.qscqlmpa.dwitch.ingame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameJoinedInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.tinylog.kotlin.Logger

class GuestInGameService : BaseInGameService() {

    override val playerRole = PlayerRole.GUEST

    override fun actionStartService(intent: Intent) {
        val gameJoinedInfo = intent.getParcelableExtra<GameJoinedInfo>(EXTRA_GAME_JOINED_INFO)!!

        Logger.info { "Start service" }
        showNotification(RoomType.WAITING_ROOM)
        app.createInGameComponents(
            playerRole,
            RoomType.WAITING_ROOM,
            gameJoinedInfo.gameLocalId,
            gameJoinedInfo.localPlayerLocalId,
            gameJoinedInfo.gamePort,
            gameJoinedInfo.gameIpAddress
        )
        app.guestFacade().connect()
        Logger.info { "Service started" }
        notifyServiceStarted()
    }

    override fun actionChangeRoomToGameRoom() {
        Logger.info { "Go to game room" }
        showNotification(RoomType.GAME_ROOM)
    }

    override fun cleanUp() {
        app.guestFacade().disconnect()
        app.destroyInGameComponents()
        app.homeFacade().deleteGamesMarkedForDeletion().blockingSubscribe()
    }

    companion object {

        private const val EXTRA_GAME_JOINED_INFO = "GameJoinedInfo"

        fun startService(
            context: Context,
            gameJoinedInfo: GameJoinedInfo
        ) {
            Logger.info { "Starting GuestService..." }
            val intent = Intent(context, GuestInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_JOINED_INFO, gameJoinedInfo)
            context.startService(intent)
        }

        fun goChangeRoomToGameRoom(context: Context) {
            Logger.info { "Changing room to Game Room..." }
            val intent = createIntent(context, GuestInGameService::class.java, ACTION_CHANGE_ROOM_TO_GAME_ROOM)
            context.startService(intent)
        }

        fun stopService(context: Context) {
            Logger.info { "Stopping GuestService..." }
            val intent = Intent(context, GuestInGameService::class.java)
            context.stopService(intent)
        }
    }
}
