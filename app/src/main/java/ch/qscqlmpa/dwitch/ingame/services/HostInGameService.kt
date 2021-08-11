package ch.qscqlmpa.dwitch.ingame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchgame.gameadvertising.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GameCreatedInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.tinylog.kotlin.Logger

class HostInGameService : BaseInGameService() {

    private val gameAdvertisingDisposable = DisposableManager()

    override val playerRole = PlayerRole.HOST

    override fun actionStartService(intent: Intent) {
        val gameCreatedInfo = intent.getParcelableExtra<GameCreatedInfo>(EXTRA_GAME_CREATED_INFO)!!

        Logger.info { "Start service" }
        showNotification(RoomType.WAITING_ROOM)
        app.createInGameComponents(
            playerRole,
            RoomType.WAITING_ROOM,
            gameCreatedInfo.gameLocalId,
            gameCreatedInfo.localPlayerLocalId,
            gameCreatedInfo.gamePort,
            "0.0.0.0"
        )
        app.hostFacade().startServer()
        advertiseGame(
            GameAdvertisingInfo(
                gameCreatedInfo.isNew,
                gameCreatedInfo.gameCommonId,
                gameCreatedInfo.gameName,
                gameCreatedInfo.gamePort
            )
        )

        Logger.info { "Service started" }
        notifyServiceStarted()
    }

    override fun actionChangeRoomToGameRoom() {
        Logger.info { "Go to game room" }
        showNotification(RoomType.GAME_ROOM)
        gameAdvertisingDisposable.dispose()
    }

    override fun cleanUp() {
        app.hostFacade().stopServer()
        app.destroyInGameComponents()
        gameAdvertisingDisposable.dispose()
        app.homeFacade().deleteGamesMarkedForDeletion().blockingSubscribe()
    }

    private fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo) {
        gameAdvertisingDisposable.add(
            app.hostFacade().advertiseGame(gameAdvertisingInfo).subscribe(
                {},
                { error -> Logger.error(error) { "Error while advertising the game." } }
            )
        )
    }

    companion object {

        private const val EXTRA_GAME_CREATED_INFO = "GameCreatedInfo"

        fun startService(context: Context, gameCreatedInfo: GameCreatedInfo) {
            Logger.info { "Starting HostService...()" }
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_CREATED_INFO, gameCreatedInfo)
            context.startService(intent)
        }

        fun changeRoomToGameRoom(context: Context) {
            Logger.info { "Changing room to Game Room..." }
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_CHANGE_ROOM_TO_GAME_ROOM
            context.startService(intent)
        }

        fun stopService(context: Context) {
            Logger.info { "Stopping HostService..." }
            context.stopService(Intent(context, HostInGameService::class.java))
        }
    }
}