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
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
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

        return Completable.fromAction {
            val (currentGameCommonId, currentRoom) = store.getGameCommonIdAndCurrentRoom()

            if (currentGameCommonId != msg.gameCommonId) {
                Timber.e("Game common ID provided doesn't match the current game: closing connection with client.")
                closeConnectionWithGuest(senderConnectionID)
                return@fromAction
            }

            val rejoinInfo = getRejoiningInfoIfPossible(currentGameCommonId, msg, senderConnectionID)
            if (rejoinInfo != null) {
                updatePlayer(rejoinInfo.playerLocalId, currentRoom)
                connectionStore.pairConnectionWithPlayer(senderConnectionID, rejoinInfo.playerDwitchId)
                sendMessage(HostMessageFactory.createRejoinAckMessage(rejoinInfo))
            } else {
                Timber.e("Re-joining player not found: closing connection with client.")
                closeConnectionWithGuest(senderConnectionID)
                return@fromAction
            }
            sendUpdateMessage(currentRoom)
        }
    }

    private fun getRejoiningInfoIfPossible(
        currentGameCommonId: GameCommonId,
        msg: Message.RejoinGameMessage,
        senderConnectionID: ConnectionId
    ): RejoinInfo? {
        val playerRejoiningId = store.getPlayerLocalId(msg.playerDwitchId)
        if (playerRejoiningId != null) {
            return RejoinInfo(currentGameCommonId, playerRejoiningId, msg.playerDwitchId, senderConnectionID)
        }
        return null
    }

    private fun updatePlayer(playerLocalId: Long, currentRoom: RoomType) {
        when (currentRoom) {
            RoomType.WAITING_ROOM -> store.updatePlayerWithConnectionStateAndReady(
                playerLocalId,
                PlayerConnectionState.CONNECTED,
                false
            )
            RoomType.GAME_ROOM -> store.updatePlayerWithConnectionState(playerLocalId, PlayerConnectionState.CONNECTED)
            else -> throw IllegalStateException("Current room is null !")
        }
    }

    private fun sendUpdateMessage(currentRoom: RoomType) {
        when (currentRoom) {
            RoomType.WAITING_ROOM -> sendWaitingRoomStateUpdateMessage()
            RoomType.GAME_ROOM -> sendGameStateUpdatedMessage()
            else -> throw IllegalStateException("Current room is null !")
        }
    }

    private fun sendWaitingRoomStateUpdateMessage() {
        Timber.i("Guest connected: send Waitingroom updated state to everyone.")
        sendMessage(hostMessageFactory.createWaitingRoomStateUpdateMessage())
    }

    private fun sendGameStateUpdatedMessage() {
        Timber.i("Guest connected: send game updated state to everyone.")
        val message = messageFactory.createGameStateUpdatedMessage()
        sendMessage(EnvelopeToSend(Recipient.All, message))
    }
}
