package ch.qscqlmpa.dwitchcommunication.ingame.connectionstore

import ch.qscqlmpa.dwitchcommunication.ingame.Address

internal interface ConnectionStoreInternal {
    fun addConnectionId(address: Address): ConnectionId
    fun removeConnectionIdForAddress(connectionId: ConnectionId)
    fun clearStore()
    fun getAddress(id: ConnectionId): Address?
    fun getConnectionIdForAddress(address: Address): ConnectionId?
    fun findMissingConnections(currentConnections: List<Address>): List<ConnectionId>
}
