package ch.qscqlmpa.dwitch.ingame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.ServiceIdentifier
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameJoinedInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.tinylog.kotlin.Logger

class GuestInGameService : BaseInGameService() {

    override val playerRole = PlayerRole.GUEST

    override fun actionStartService(intent: Intent) {
        val gameJoinedInfo = intent.getParcelableExtra<GameJoinedInfo>(EXTRA_GAME_JOINED_INFO)!!

        Logger.info { "Start service" }
        showNotification(RoomType.WAITING_ROOM)
        app.createInGameGuestComponents(
            gameJoinedInfo.gameLocalId,
            gameJoinedInfo.localPlayerLocalId,
            gameJoinedInfo.gamePort,
            gameJoinedInfo.gameIpAddress
        )
        idlingResource.decrement("Dagger InGame component created")
        app.guestCommunicationFacade.connect()
        Logger.info { "Service started" }
        notifyServiceStarted()
    }

    override fun actionChangeRoomToGameRoom() {
        Logger.info { "Go to game room" }
        showNotification(RoomType.GAME_ROOM)
    }

    override fun cleanUp() {
        app.guestCommunicationFacade.disconnect()
        app.destroyInGameComponents()
        app.gameLifecycleFacade.cleanUpGameResources().blockingSubscribe()
    }

    private fun notifyServiceStarted() {
        appEventRepository.notify(AppEvent.ServiceStarted(ServiceIdentifier.Guest))
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
