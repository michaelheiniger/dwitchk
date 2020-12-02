package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitchgame.appevent.GameJoinedInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import timber.log.Timber


class GuestInGameService : BaseInGameService() {

    override val playerRole = PlayerRole.GUEST

    override fun actionStartService(intent: Intent) {
        val gameJoinedInfo = intent.getParcelableExtra<GameJoinedInfo>(EXTRA_GAME_JOINED_INFO)

        Timber.i("Start service")
        showNotification(RoomType.WAITING_ROOM)
        (application as App).startOngoingGame(
            playerRole,
            RoomType.WAITING_ROOM,
            gameJoinedInfo.gameLocalId,
            gameJoinedInfo.localPlayerLocalId,
            gameJoinedInfo.gamePort,
            gameJoinedInfo.gameIpAddress
        )
        getOngoingGameComponent().guestFacade.connect()
    }

    override fun actionChangeRoomToGameRoom() {
        Timber.i("Go to game room")
        showNotification(RoomType.GAME_ROOM)
    }

    override fun cleanUp() {
        getOngoingGameComponent().guestFacade.closeConnection()
        (application as App).stopOngoingGame()
    }

    companion object {

        private const val EXTRA_GAME_JOINED_INFO = "GameJoinedInfo"

        fun startService(
            context: Context,
            gameJoinedInfo: GameJoinedInfo
        ) {
            Timber.i("Starting GuestService...()")
            val intent = Intent(context, GuestInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_JOINED_INFO, gameJoinedInfo)
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
