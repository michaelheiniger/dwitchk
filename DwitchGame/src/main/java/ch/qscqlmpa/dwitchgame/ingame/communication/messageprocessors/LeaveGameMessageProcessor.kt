package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class LeaveGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.LeaveGameMessage

        val communicator = communicatorLazy.get()

        return Completable.fromAction {
            deletePlayerFromStore(msg.dwitchPlayerId)
            communicator.sendMessage(hostMessageFactory.createWaitingRoomStateUpdateMessage())
        }
    }

    private fun deletePlayerFromStore(dwitchPlayerId: DwitchPlayerId) {
        val numRecordsAffected = store.deletePlayer(dwitchPlayerId)
        if (numRecordsAffected != 1) {
            throw IllegalStateException("Player with in-game ID $dwitchPlayerId is leaving game but is not found in store.")
        } else {
            Logger.info { "Player with in-game ID $dwitchPlayerId was deleted because it is leaving the game." }
        }
    }
}
