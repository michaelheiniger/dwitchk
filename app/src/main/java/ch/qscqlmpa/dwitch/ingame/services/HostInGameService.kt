package ch.qscqlmpa.dwitch.ingame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.ServiceIdentifier
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameCreatedInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.tinylog.kotlin.Logger

class HostInGameService : BaseInGameService() {

    private val gameAdvertisingDisposable = DisposableManager()

    override val playerRole = PlayerRole.HOST

    override fun onCreate() {
        app.inject(this)
        super.onCreate()
    }

    override fun actionStartService(intent: Intent) {
        val gameCreatedInfo = intent.getParcelableExtra<GameCreatedInfo>(EXTRA_GAME_CREATED_INFO)!!

        Logger.info { "Start service" }
        showNotification(RoomType.WAITING_ROOM)
        app.createInGameHostComponents(
            gameCreatedInfo.gameLocalId,
            gameCreatedInfo.localPlayerLocalId
        )
        app.hostCommunicationFacade!!.startServer()
        advertiseGame()

        Logger.info { "Service started" }
        notifyServiceStarted()
    }

    override fun actionChangeRoomToGameRoom() {
        Logger.info { "Go to game room" }
        gameAdvertisingDisposable.disposeAndReset()
        // Now that the game has started, it is advertised as an existing game.
        // This is needed for players to rejoin the game.
        advertiseGame()
        showNotification(RoomType.GAME_ROOM)
    }

    override fun cleanUp() {
        app.hostCommunicationFacade?.stopServer()
        app.destroyInGameComponents()
        gameAdvertisingDisposable.dispose()
        app.gameLifecycleFacade.cleanUpGameResources().blockingSubscribe()
    }

    private fun notifyServiceStarted() {
        appEventRepository.notify(AppEvent.ServiceStarted(ServiceIdentifier.Host))
    }

    private fun advertiseGame() {
        gameAdvertisingDisposable.add(
            app.gameAdvertisingFacade.advertiseGame().subscribe(
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
