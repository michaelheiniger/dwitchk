package ch.qscqlmpa.dwitch.ongoinggame.services

import android.content.Context
import android.content.Intent
import ch.qscqlmpa.dwitch.app.App
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchgame.appevent.GameCreatedInfo
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.gamediscovery.GameAdvertisingInfo
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import timber.log.Timber


class HostInGameService : BaseInGameService() {

    private val gameAdvertisingDisposable = DisposableManager()

    override val playerRole = PlayerRole.HOST

    override fun actionStartService(intent: Intent) {
        val gameCreatedInfo = intent.getParcelableExtra<GameCreatedInfo>(EXTRA_GAME_CREATED_INFO)

        Timber.i("Start service")
        showNotification(RoomType.WAITING_ROOM)
        (application as App).startOngoingGame(
            playerRole,
            RoomType.WAITING_ROOM,
            gameCreatedInfo.gameLocalId,
            gameCreatedInfo.localPlayerLocalId,
            gameCreatedInfo.gamePort,
            "0.0.0.0"
        )
        getOngoingGameComponent().hostFacade.listenForConnections()
        advertiseGame(GameAdvertisingInfo(gameCreatedInfo.gameCommonId, gameCreatedInfo.gameName, gameCreatedInfo.gamePort))
    }

    override fun actionChangeRoomToGameRoom() {
        Timber.i("Go to game room")
        showNotification(RoomType.GAME_ROOM)
        gameAdvertisingDisposable.dispose()
    }

    override fun cleanUp() {
        getOngoingGameComponent().hostFacade.closeAllConnections()
        (application as App).stopOngoingGame()
        gameAdvertisingDisposable.dispose()
    }

    private fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo) {
        gameAdvertisingDisposable.add(
            getOngoingGameComponent().hostFacade.advertiseGame(gameAdvertisingInfo).subscribe()
        )
    }

    companion object {

        private const val EXTRA_GAME_CREATED_INFO = "GameCreatedInfo"

        fun startService(context: Context, gameCreatedInfo: GameCreatedInfo) {
            Timber.i("Starting HostService...()")
            val intent = Intent(context, HostInGameService::class.java)
            intent.action = ACTION_START_SERVICE
            intent.putExtra(EXTRA_GAME_CREATED_INFO, gameCreatedInfo)
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
