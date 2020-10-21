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
import timber.log.Timber
import javax.inject.Inject

internal class LeaveGameMessageProcessor @Inject constructor(private val store: InGameStore,
                                                             private val localConnectionIdStore: LocalConnectionIdStore,
                                                             private val hostMessageFactory: HostMessageFactory,
                                                             private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.LeaveGameMessage

        localConnectionIdStore.removeLocalConnectionId(senderLocalConnectionID)

        val communicator = communicatorLazy.get()

        return deletePlayerFromStore(msg.playerInGameId)
                .andThen(hostMessageFactory.createWaitingRoomStateUpdateMessage())
                .flatMapCompletable(communicator::sendMessage)
    }

    private fun deletePlayerFromStore(playerInGameId: PlayerInGameId): Completable {
        return Completable.fromAction {
            val numRecordsAffected = store.deletePlayer(playerInGameId)
            if (numRecordsAffected != 1) {
                throw IllegalStateException("Player with in-game ID $playerInGameId is leaving game but is not found in store.")
            } else {
                Timber.i("Player with in-game ID $playerInGameId was deleted because it is leaving the game.")
            }
        }
    }
}