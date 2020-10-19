package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import dagger.Lazy
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class PlayerReadyMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

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