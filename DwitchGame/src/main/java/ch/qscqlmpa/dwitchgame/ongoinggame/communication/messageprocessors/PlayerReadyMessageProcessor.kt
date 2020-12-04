package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject

internal class PlayerReadyMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        message as Message.PlayerReadyMessage

        return updatePlayerWithReady(message)
                .andThen(hostMessageFactory.createWaitingRoomStateUpdateMessage())
                .flatMapCompletable { msg -> communicatorLazy.get().sendMessage(msg) }
    }

    private fun updatePlayerWithReady(message: Message.PlayerReadyMessage): Completable {
        return Completable.fromCallable {
            val numRecordsAffected = store.updatePlayerWithReady(message.playerInGameId, message.ready)
            if (numRecordsAffected != 1) {
                throw IllegalStateException("State of player with in-game ID $${message.playerInGameId} could not be updated because not found in store.")
            } else {
                Timber.i("Player with in-game ID ${message.playerInGameId} changed ready state to ${message.ready}.")
            }
        }
    }
}