 package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
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
                val newPlayerLocalId = store.insertNewGuestPlayer(msg.playerName)
                storeConnectionId(senderConnectionID, newPlayerLocalId)
                sendMessages(senderConnectionID, newPlayerLocalId)
            } else {
                // Not supposed to happen since the list of advertised games is filtered to show only relevant games.
                Timber.w("A player has tried to join a game to be resumed by joining it instead of rejoining it. Kicking player...")
                closeConnectionWithGuest(senderConnectionID)
            }
        }
    }

    private fun sendMessages(
        senderConnectionId: ConnectionId,
        newPlayerLocalId: Long
    ) = sendMessages(
        listOf(
            hostMessageFactory.createJoinAckMessage(senderConnectionId, PlayerDwitchId(newPlayerLocalId)),
            hostMessageFactory.createWaitingRoomStateUpdateMessage()
        )
    )

    private fun storeConnectionId(senderConnectionId: ConnectionId, newPlayerLocalId: Long) {
        val newGuestPlayerDwitchId = store.getPlayerDwitchId(newPlayerLocalId)
        connectionStore.pairConnectionWithPlayer(senderConnectionId, newGuestPlayerDwitchId)
    }
}