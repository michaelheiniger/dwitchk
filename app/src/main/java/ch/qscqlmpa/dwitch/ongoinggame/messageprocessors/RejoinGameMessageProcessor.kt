package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Maybe
import timber.log.Timber
import javax.inject.Inject

internal class RejoinGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    communicatorImplLazy: Lazy<HostCommunicator>,
    private val hostMessageFactory: HostMessageFactory,
    private val localConnectionIdStore: LocalConnectionIdStore
) : BaseHostProcessor(communicatorImplLazy) {

    override fun process(
        message: Message,
        senderLocalConnectionID: LocalConnectionId
    ): Completable {

        val msg = message as Message.RejoinGameMessage

        return getRejoiningInfoIfPossible(msg, senderLocalConnectionID)
            .doOnSuccess { rejoinInfo ->
                updatePlayer(rejoinInfo.player)
                updateConnectionIdStore(rejoinInfo.player, senderLocalConnectionID)
            }
            .flatMapCompletable { rejoinInfo ->
                sendRejoinAck(rejoinInfo)
                    .andThen(sendWaitingRoomStateUpdateMessage())
            }
    }

    private fun getRejoiningInfoIfPossible(
        msg: Message.RejoinGameMessage,
        senderLocalConnectionID: LocalConnectionId
    ): Maybe<RejoinInfo> {
        return Maybe.fromCallable {

            val gameCommonId = store.getGame().gameCommonId
            val playerRejoining = store.getPlayer(msg.playerInGameId)

            if (gameCommonId != msg.gameCommonId) {
                Timber.e("Game common ID provided doesn't match: closing connection with client.")
                closeConnectionWithGuest(senderLocalConnectionID)
                return@fromCallable null
            }

            if (playerRejoining == null) {
                Timber.e("Re-joining player not found: closing connection with client.")
                closeConnectionWithGuest(senderLocalConnectionID)
                return@fromCallable null
            }

            return@fromCallable RejoinInfo(gameCommonId, playerRejoining, senderLocalConnectionID)
        }
    }

    private fun updatePlayer(player: Player) {
        store.updatePlayerWithConnectionStateAndReady(
            player.id,
            PlayerConnectionState.CONNECTED,
            false
        )
    }

    private fun updateConnectionIdStore(player: Player, connectionID: LocalConnectionId) {
        localConnectionIdStore.mapPlayerIdToConnectionId(connectionID, player.inGameId)
    }

    private fun sendRejoinAck(rejoinInfo: RejoinInfo): Completable {
        return sendMessage(HostMessageFactory.createRejoinAckMessage(rejoinInfo))
    }

    private fun sendWaitingRoomStateUpdateMessage(): Completable {
        return hostMessageFactory.createWaitingRoomStateUpdateMessage()
            .flatMapCompletable(::sendMessage)
    }
}