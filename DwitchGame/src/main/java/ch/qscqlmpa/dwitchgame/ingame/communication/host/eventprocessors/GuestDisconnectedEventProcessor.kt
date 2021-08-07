package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.websocket.ServerEvent
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
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

    override fun process(event: ServerEvent.CommunicationEvent): Completable {

        event as ServerEvent.CommunicationEvent.ClientDisconnected

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

    private fun updatePlayerWithDisconnectedState(dwitchPlayerId: DwitchPlayerId, connectionId: ConnectionId) {
        val numRecordsAffected = store.updatePlayer(dwitchPlayerId, connected = false, ready = false)

        if (numRecordsAffected != 1) {
            throw IllegalStateException("State of player with connection ID $connectionId could not be updated because not found in store.")
        } else {
            Logger.info { "Player with connection ID $connectionId is now disconnected." }
        }
    }
}
