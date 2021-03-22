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
import org.tinylog.kotlin.Logger
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
        Logger.info { "Client disconnected with unknown connection ID." }
        return Completable.complete()
    }

    private fun handleEventWhenConnectionIdIsKnown(connectionId: ConnectionId): Completable {
        return Completable.fromAction {
            val playerDwitchId = connectionStore.getDwitchId(connectionId)
            connectionStore.removeConnectionIdForDwitchId(connectionId)
            Logger.info { "Client disconnected with connection ID $connectionId and dwitch ID $playerDwitchId" }

            if (playerDwitchId != null) {
                updatePlayerWithDisconnectedState(playerDwitchId, connectionId)
                val message = hostMessageFactory.createWaitingRoomStateUpdateMessage()
                communicatorLazy.get().sendMessage(message)
            } else {
                Logger.info { "ClientDisconnected event: no player in-game ID found for connection ID $connectionId" }
            }
        }
    }

    private fun updatePlayerWithDisconnectedState(playerDwitchId: PlayerDwitchId, connectionId: ConnectionId) {
        val newState = PlayerConnectionState.DISCONNECTED
        val numRecordsAffected = store.updatePlayer(playerDwitchId, newState, false)

        if (numRecordsAffected != 1) {
            throw IllegalStateException("State of player with connection ID $connectionId could not be updated because not found in store.")
        } else {
            Logger.info { "Player with connection ID $connectionId changed state to $newState." }
        }
    }
}
