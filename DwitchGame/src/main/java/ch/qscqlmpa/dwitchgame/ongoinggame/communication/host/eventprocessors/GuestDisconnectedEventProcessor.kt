package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject

internal class GuestDisconnectedEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val connectionStore: ConnectionStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ClientDisconnected

        if (event.connectionId != null) {
            return handleEventWhenConnectionIdIsKnown(event.connectionId!!) //TODO: Use "let" to remove the "!!" ?
        }
        Timber.i("Client disconnected with unknown connection ID.")
        return Completable.complete()
    }

    private fun handleEventWhenConnectionIdIsKnown(connectionId: ConnectionId): Completable {
        val playerInGameId = connectionStore.getInGameId(connectionId)
        val senderAddress = connectionStore.getAddress(connectionId)

        connectionStore.removeConnectionId(connectionId)
        Timber.i("Client disconnected with connection ID: %s", senderAddress)

        return if (playerInGameId != null) {
            val communicator = communicatorLazy.get()
            updatePlayerWithDisconnectedState(playerInGameId, senderAddress)
                .andThen(hostMessageFactory.createWaitingRoomStateUpdateMessage())
                .flatMapCompletable(communicator::sendMessage)
        } else {
            Timber.i(
                "ClientDisconnected event: no player in-game ID found for connection ID %s",
                senderAddress
            )
            Completable.complete()
        }
    }

    private fun updatePlayerWithDisconnectedState(
        playerInGameId: PlayerInGameId,
        address: Address?
    ): Completable {
        return Completable.fromAction {
            val newState = PlayerConnectionState.DISCONNECTED
            val numRecordsAffected = store.updatePlayer(playerInGameId, newState, false)

            if (numRecordsAffected != 1) {
                throw IllegalStateException("State of player with connection ID $address could not be updated because not found in store.")
            } else {
                Timber.i("Player with connection ID $address changed state to $newState.")
            }
        }
    }
}