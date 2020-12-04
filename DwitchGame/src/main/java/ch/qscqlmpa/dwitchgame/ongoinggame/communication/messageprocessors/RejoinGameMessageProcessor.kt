package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RejoinInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import timber.log.Timber
import javax.inject.Inject

internal class RejoinGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    communicatorImplLazy: Lazy<HostCommunicator>,
    private val hostMessageFactory: HostMessageFactory,
    private val connectionStore: ConnectionStore
) : BaseHostProcessor(communicatorImplLazy) {

    override fun process(
        message: Message,
        senderConnectionID: ConnectionId
    ): Completable {

        val msg = message as Message.RejoinGameMessage

        return getRejoiningInfoIfPossible(msg, senderConnectionID)
            .doOnSuccess { rejoinInfo ->
                updatePlayer(rejoinInfo.player)
                updateConnectionStore(rejoinInfo.player, senderConnectionID)
            }
            .flatMapCompletable { rejoinInfo ->
                sendRejoinAck(rejoinInfo)
                    .andThen(sendWaitingRoomStateUpdateMessage())
            }
    }

    private fun getRejoiningInfoIfPossible(
        msg: Message.RejoinGameMessage,
        senderConnectionID: ConnectionId
    ): Maybe<RejoinInfo> {
        return Maybe.fromCallable {

            val gameCommonId = store.getGame().gameCommonId
            val playerRejoining = store.getPlayer(msg.playerInGameId)

            if (gameCommonId != msg.gameCommonId) {
                Timber.e("Game common ID provided doesn't match: closing connection with client.")
                closeConnectionWithGuest(senderConnectionID)
                return@fromCallable null
            }

            if (playerRejoining == null) {
                Timber.e("Re-joining player not found: closing connection with client.")
                closeConnectionWithGuest(senderConnectionID)
                return@fromCallable null
            }

            return@fromCallable RejoinInfo(gameCommonId, playerRejoining, senderConnectionID)
        }
    }

    private fun updatePlayer(player: Player) {
        store.updatePlayerWithConnectionStateAndReady(
            player.id,
            PlayerConnectionState.CONNECTED,
            false
        )
    }

    private fun updateConnectionStore(player: Player, connectionID: ConnectionId) {
        connectionStore.mapPlayerIdToConnectionId(connectionID, player.inGameId)
    }

    private fun sendRejoinAck(rejoinInfo: RejoinInfo): Completable {
        return sendMessage(HostMessageFactory.createRejoinAckMessage(rejoinInfo))
    }

    private fun sendWaitingRoomStateUpdateMessage(): Completable {
        return hostMessageFactory.createWaitingRoomStateUpdateMessage()
            .flatMapCompletable(::sendMessage)
    }
}