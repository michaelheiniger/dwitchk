package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject

internal class LeaveGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.LeaveGameMessage

        //TODO: REmove connectionId - playerInGameId mapping from connectionstore ?

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