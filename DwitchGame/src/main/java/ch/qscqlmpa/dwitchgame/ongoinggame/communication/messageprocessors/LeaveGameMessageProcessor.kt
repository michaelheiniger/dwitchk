package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
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

        val communicator = communicatorLazy.get()

        return Completable.fromAction {
            deletePlayerFromStore(msg.playerDwitchId)
            communicator.sendMessage(hostMessageFactory.createWaitingRoomStateUpdateMessage())
        }
    }

    private fun deletePlayerFromStore(playerDwitchId: PlayerDwitchId) {
        val numRecordsAffected = store.deletePlayer(playerDwitchId)
        if (numRecordsAffected != 1) {
            throw IllegalStateException("Player with in-game ID $playerDwitchId is leaving game but is not found in store.")
        } else {
            Timber.i("Player with in-game ID $playerDwitchId was deleted because it is leaving the game.")
        }
    }
}
