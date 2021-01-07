package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.model.RejoinInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject

internal class RejoinGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    communicatorImplLazy: Lazy<HostCommunicator>,
    private val hostMessageFactory: HostMessageFactory,
    private val messageFactory: MessageFactory,
    private val connectionStore: ConnectionStore
) : BaseHostProcessor(communicatorImplLazy) {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.RejoinGameMessage

        return Single.fromCallable { store.getCurrentRoom() }
            .flatMapCompletable { currentRoom ->
                getRejoiningInfoIfPossible(msg, senderConnectionID)
                    .doOnSuccess { rejoinInfo ->
                        updatePlayer(rejoinInfo.player, currentRoom)
                        updateConnectionStore(rejoinInfo.player, senderConnectionID)
                    }
                    .flatMapCompletable { rejoinInfo ->
                        sendRejoinAck(rejoinInfo)
                            .andThen(sendMessageAccordingToCurrentRoom())
                    }
            }
    }

    private fun getRejoiningInfoIfPossible(
        msg: Message.RejoinGameMessage,
        senderConnectionID: ConnectionId
    ): Maybe<RejoinInfo> {
        return Maybe.fromCallable {

            val gameCommonId = store.getGame().gameCommonId
            val playerRejoining = store.getPlayer(msg.playerDwitchId)

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

    private fun updatePlayer(player: Player, currentRoom: RoomType) {
        when (currentRoom) {
            RoomType.WAITING_ROOM -> store.updatePlayerWithConnectionStateAndReady(player.id, PlayerConnectionState.CONNECTED, false)
            RoomType.GAME_ROOM -> store.updatePlayerWithConnectionState(player.id, PlayerConnectionState.CONNECTED)
            else -> throw IllegalStateException("Current room is null !")
        }
    }

    private fun updateConnectionStore(player: Player, connectionID: ConnectionId) {
        connectionStore.pairConnectionWithPlayer(connectionID, player.dwitchId)
    }

    private fun sendRejoinAck(rejoinInfo: RejoinInfo): Completable {
        return sendMessage(HostMessageFactory.createRejoinAckMessage(rejoinInfo))
    }

    private fun sendMessageAccordingToCurrentRoom(): Completable {
        return Single.fromCallable { store.getCurrentRoom() }
            .flatMapCompletable { currentRoom ->
                when (currentRoom) {
                    RoomType.WAITING_ROOM -> sendWaitingRoomStateUpdateMessage()
                    RoomType.GAME_ROOM -> sendGameStateUpdatedMessage()
                    else -> throw IllegalStateException("Current room is null !")
                }
            }
    }

    private fun sendWaitingRoomStateUpdateMessage(): Completable {
        Timber.i("Guest connected: send Waitingroom updated state to everyone.")
        return hostMessageFactory.createWaitingRoomStateUpdateMessage()
            .flatMapCompletable(::sendMessage)
    }

    private fun sendGameStateUpdatedMessage(): Completable {
        Timber.i("Guest connected: send game updated state to everyone.")
        return messageFactory.createGameStateUpdatedMessage()
            .map { message -> EnvelopeToSend(Recipient.All, message) }
            .flatMapCompletable(this::sendMessage)
    }
}