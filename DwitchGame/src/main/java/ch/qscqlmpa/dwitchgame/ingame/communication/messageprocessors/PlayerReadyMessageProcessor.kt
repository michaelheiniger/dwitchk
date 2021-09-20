package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
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
            Logger.info { "Player with in-game ID ${message.playerId} changed ready state to ${message.ready}." }
        }
    }
}
