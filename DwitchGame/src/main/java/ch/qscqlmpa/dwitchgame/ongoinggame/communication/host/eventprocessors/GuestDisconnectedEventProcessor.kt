package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
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
            return handleEventWhenConnectionIdIsKnown(event.connectionId!!)
        }
        Timber.i("Client disconnected with unknown connection ID.")
        return Completable.complete()
    }

    private fun handleEventWhenConnectionIdIsKnown(connectionId: ConnectionId): Completable {
        val playerDwitchId = connectionStore.getDwitchId(connectionId)
        connectionStore.removeConnectionIdForDwitchId(connectionId)
        Timber.i("Client disconnected with connection ID $connectionId and dwitch ID $playerDwitchId")

        return if (playerDwitchId != null) {
            val communicator = communicatorLazy.get()
            updatePlayerWithDisconnectedState(playerDwitchId, connectionId)
                .andThen(hostMessageFactory.createWaitingRoomStateUpdateMessage())
                .flatMapCompletable(communicator::sendMessage)
        } else {
            Timber.i("ClientDisconnected event: no player in-game ID found for connection ID $connectionId")
            Completable.complete()
        }
    }

    private fun updatePlayerWithDisconnectedState(playerDwitchId: PlayerDwitchId, connectionId: ConnectionId): Completable {
        return Completable.fromAction {
            val newState = PlayerConnectionState.DISCONNECTED
            val numRecordsAffected = store.updatePlayer(playerDwitchId, newState, false)

            if (numRecordsAffected != 1) {
                throw IllegalStateException("State of player with connection ID $connectionId could not be updated because not found in store.")
            } else {
                Timber.i("Player with connection ID $connectionId changed state to $newState.")
            }
        }
    }
}