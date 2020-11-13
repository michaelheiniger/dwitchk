package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.Address
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import dagger.Lazy
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class GuestDisconnectedEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val localConnectionIdStore: LocalConnectionIdStore,
    private val hostMessageFactory: HostMessageFactory,
    private val communicatorLazy: Lazy<HostCommunicator>
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ClientDisconnected

        if (event.localConnectionId != null) {
            return handleEventWhenConnectionIdIsKnown(event.localConnectionId)
        }
        Timber.i("Client disconnected with unknown connection ID.")
        return Completable.complete()
    }

    private fun handleEventWhenConnectionIdIsKnown(localConnectionId: LocalConnectionId): Completable {
        val playerInGameId = localConnectionIdStore.getInGameId(localConnectionId)
        val senderAddress = localConnectionIdStore.getAddress(localConnectionId)

        localConnectionIdStore.removeConnectionId(localConnectionId)
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