package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

internal class RejoinGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicatorImplLazy: Lazy<HostCommunicator>,
    private val hostMessageFactory: HostMessageFactory,
    private val localConnectionIdStore: LocalConnectionIdStore
) : MessageProcessor {

    override fun process(
        message: Message,
        senderLocalConnectionID: LocalConnectionId
    ): Completable {

        val communicator = communicatorImplLazy.get()

        return Maybe.fromCallable {
            val msg = message as Message.RejoinGameMessage
            val playerRejoining = store.getPlayer(msg.playerInGameId)

            if (playerRejoining != null) {
                return@fromCallable playerRejoining
            } else {
                communicator.closeConnectionWithClient(senderLocalConnectionID)
                return@fromCallable null
            }
        }.flatMapCompletable { playerRejoining ->
            Completable.fromAction {
                store.updatePlayerWithConnectionStateAndReady(
                    playerRejoining.id,
                    PlayerConnectionState.CONNECTED,
                    false
                )
                localConnectionIdStore.addPlayerInGameId(
                    senderLocalConnectionID,
                    playerRejoining.inGameId
                )
            }.andThen(
                hostMessageFactory.createWaitingRoomStateUpdateMessage()
                    .flatMapCompletable(communicator::sendMessage)
            )
        }
    }
}