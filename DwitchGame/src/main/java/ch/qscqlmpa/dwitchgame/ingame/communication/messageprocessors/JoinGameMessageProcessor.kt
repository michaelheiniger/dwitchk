package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class JoinGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    communicatorLazy: Lazy<HostCommunicator>,
    private val hostMessageFactory: HostMessageFactory,
    private val connectionStore: ConnectionStore
) : BaseHostProcessor(communicatorLazy) {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.JoinGameMessage

        return Completable.fromAction {
            if (store.gameIsNew()) {
                val newPlayerLocalId = store.insertNewGuestPlayer(msg.playerName, msg.computerManaged)
                storeConnectionId(senderConnectionID, newPlayerLocalId)
                sendMessages(senderConnectionID, newPlayerLocalId)
            } else {
                // Not supposed to happen since the list of advertised games is filtered to show only relevant games.
                Logger.warn { "A player has tried to join a game to be resumed by joining it instead of rejoining it. Kicking player..." }
                closeConnectionWithGuest(senderConnectionID)
            }
        }
    }

    private fun sendMessages(
        senderConnectionId: ConnectionId,
        newPlayerLocalId: Long
    ) = sendMessages(
        listOf(
            hostMessageFactory.createJoinAckMessage(senderConnectionId, DwitchPlayerId(newPlayerLocalId)),
            hostMessageFactory.createWaitingRoomStateUpdateMessage()
        )
    )

    private fun storeConnectionId(senderConnectionId: ConnectionId, newPlayerLocalId: Long) {
        val newGuestPlayerDwitchId = store.getPlayerDwitchId(newPlayerLocalId)
        connectionStore.pairConnectionWithPlayer(senderConnectionId, newGuestPlayerDwitchId)
    }
}
