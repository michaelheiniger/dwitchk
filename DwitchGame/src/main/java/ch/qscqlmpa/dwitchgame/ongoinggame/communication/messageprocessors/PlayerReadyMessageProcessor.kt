package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import javax.inject.Inject

internal class PlayerReadyMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        message as Message.PlayerReadyMessage

        return Completable.fromAction {
            updatePlayerWithReady(message)
            val wrStateUpdateMessage = hostMessageFactory.createWaitingRoomStateUpdateMessage()
            communicatorLazy.get().sendMessage(wrStateUpdateMessage)
        }
    }

    private fun updatePlayerWithReady(message: Message.PlayerReadyMessage) {
        val numRecordsAffected = store.updatePlayerWithReady(message.playerId, message.ready)
        if (numRecordsAffected != 1) {
            throw IllegalStateException("State of player with in-game ID $${message.playerId} could not be updated because not found in store.")
        } else {
            logger.info { "Player with in-game ID ${message.playerId} changed ready state to ${message.ready}." }
        }
    }

    companion object : KLogging()
}
