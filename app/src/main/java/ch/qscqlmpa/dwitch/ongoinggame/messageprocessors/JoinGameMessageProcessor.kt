package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class JoinGameMessageProcessor @Inject constructor(private val store: InGameStore,
                                                            communicatorLazy: Lazy<HostCommunicator>,
                                                            private val hostMessageFactory: HostMessageFactory,
                                                            private val localConnectionIdStore: LocalConnectionIdStore
) : BaseHostProcessor(communicatorLazy) {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.JoinGameMessage

        return insertNewGuestPlayer(msg.playerName)
                .flatMapCompletable { newPlayerLocalId ->
                    Completable.merge(listOf(
                            storeConnectionIdentifier(senderLocalConnectionID, newPlayerLocalId),
                            sendMessages(listOf(
                                    hostMessageFactory.createJoinAckMessage(
                                        senderLocalConnectionID,
                                        PlayerInGameId(newPlayerLocalId)
                                    ),
                                    hostMessageFactory.createWaitingRoomStateUpdateMessage()
                            ))
                    ))
                }
    }

    private fun insertNewGuestPlayer(playerName: String): Single<Long> {
        return Single.fromCallable { store.insertNewGuestPlayer(playerName) }
    }

    private fun storeConnectionIdentifier(senderLocalConnectionID: LocalConnectionId, newPlayerLocalId: Long): Completable {
        return Completable.fromAction {
            val newGuestPlayerInGameId = store.getPlayerInGameId(newPlayerLocalId)
            localConnectionIdStore.addPlayerInGameId(senderLocalConnectionID, newGuestPlayerInGameId)
        }
    }
}