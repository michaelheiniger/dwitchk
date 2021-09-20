package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.Recipient
import ch.qscqlmpa.dwitchcommunication.ingame.model.RejoinInfo
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
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
                Logger.error { "Game common ID provided doesn't match the current game: closing connection with client." }
                closeConnectionWithGuest(senderConnectionID)
                return@fromAction
            }

            val rejoinInfo = getRejoiningInfoIfPossible(currentGameCommonId, currentRoom, msg, senderConnectionID)
            if (rejoinInfo != null) {
                updatePlayer(rejoinInfo.playerLocalId, currentRoom)
                connectionStore.pairConnectionWithPlayer(senderConnectionID, rejoinInfo.dwitchPlayerId)
                sendMessage(HostMessageFactory.createRejoinAckMessage(rejoinInfo))
            } else {
                Logger.error { "Re-joining player not found: closing connection with client." }
                closeConnectionWithGuest(senderConnectionID)
                return@fromAction
            }
            sendUpdateMessages(currentRoom)
        }
    }

    private fun getRejoiningInfoIfPossible(
        currentGameCommonId: GameCommonId,
        currentRoom: RoomType,
        msg: Message.RejoinGameMessage,
        senderConnectionID: ConnectionId
    ): RejoinInfo? {
        val playerRejoiningId = store.getPlayerLocalId(msg.dwitchPlayerId)
        if (playerRejoiningId != null) {
            return RejoinInfo(currentGameCommonId, currentRoom, playerRejoiningId, msg.dwitchPlayerId, senderConnectionID)
        }
        return null
    }

    private fun updatePlayer(playerLocalId: Long, currentRoom: RoomType) {
        when (currentRoom) {
            RoomType.WAITING_ROOM -> store.updatePlayerWithConnectionStateAndReady(
                playerLocalId = playerLocalId,
                connected = true,
                ready = false
            )
            RoomType.GAME_ROOM -> store.updatePlayerWithConnectionState(playerLocalId, connected = true)
            else -> throw IllegalStateException("Current room is null !")
        }
    }

    private fun sendUpdateMessages(currentRoom: RoomType) {
        Logger.info { "Guest connected: send Waitingroom updated state to everyone." }
        sendMessage(hostMessageFactory.createWaitingRoomStateUpdateMessage())
        if (currentRoom == RoomType.GAME_ROOM) sendGameStateUpdatedMessage()
    }

    private fun sendGameStateUpdatedMessage() {
        Logger.info { "Guest connected: send game updated state to everyone." }
        val message = messageFactory.createGameStateUpdatedMessage()
        sendMessage(EnvelopeToSend(Recipient.All, message))
    }
}
