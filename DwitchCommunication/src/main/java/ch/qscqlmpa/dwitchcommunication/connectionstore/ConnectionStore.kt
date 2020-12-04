package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId


interface ConnectionStore {

    fun addConnectionId(address: Address): ConnectionId
    fun removeConnectionId(connectionId: ConnectionId)
    fun mapPlayerIdToConnectionId(connectionId: ConnectionId, playerInGameId: PlayerInGameId)
    fun getLocalConnectionIdForAddress(address: Address): ConnectionId?
    fun getAddress(id: ConnectionId): Address?
    fun getInGameId(id: ConnectionId): PlayerInGameId?
    fun findMissingConnections(currentConnections: List<Address>): List<ConnectionId>
}