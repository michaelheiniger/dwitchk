package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
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

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.JoinGameMessage

        return insertNewGuestPlayer(msg.playerName)
            .flatMapCompletable { newPlayerLocalId ->
                Completable.merge(
                    listOf(
                        storeConnectionIdentifier(senderLocalConnectionID, newPlayerLocalId),
                        sendMessages(
                            listOf(
                                hostMessageFactory.createJoinAckMessage(
                                    senderLocalConnectionID,
                                    PlayerInGameId(newPlayerLocalId)
                                ),
                                hostMessageFactory.createWaitingRoomStateUpdateMessage()
                            )
                        )
                    )
                )
            }
    }

    private fun insertNewGuestPlayer(playerName: String): Single<Long> {
        return Single.fromCallable { store.insertNewGuestPlayer(playerName) }
    }

    private fun storeConnectionIdentifier(senderLocalConnectionID: LocalConnectionId, newPlayerLocalId: Long): Completable {
        return Completable.fromAction {
            val newGuestPlayerInGameId = store.getPlayerInGameId(newPlayerLocalId)
            connectionStore.mapPlayerIdToConnectionId(senderLocalConnectionID, newGuestPlayerInGameId)
        }
    }
}