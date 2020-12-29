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
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class JoinGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    communicatorLazy: Lazy<HostCommunicator>,
    private val hostMessageFactory: HostMessageFactory,
    private val connectionStore: ConnectionStore
) : BaseHostProcessor(communicatorLazy) {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.JoinGameMessage

        return insertNewGuestPlayer(msg.playerName)
            .flatMapCompletable { newPlayerLocalId ->
                Completable.merge(
                    listOf(
                        storeConnectionId(senderConnectionID, newPlayerLocalId),
                        sendMessages(senderConnectionID, newPlayerLocalId)
                    )
                )
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

    private fun insertNewGuestPlayer(playerName: String): Single<Long> {
        return Single.fromCallable { store.insertNewGuestPlayer(playerName) }
    }

    private fun storeConnectionId(senderConnectionId: ConnectionId, newPlayerLocalId: Long): Completable {
        return Completable.fromAction {
            val newGuestPlayerDwitchId = store.getPlayerDwitchId(newPlayerLocalId)
            connectionStore.pairConnectionWithPlayer(senderConnectionId, newGuestPlayerDwitchId)
        }
    }
}