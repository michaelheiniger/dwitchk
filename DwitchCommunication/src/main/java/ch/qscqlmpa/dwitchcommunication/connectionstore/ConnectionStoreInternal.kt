package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address

internal interface ConnectionStoreInternal {
    fun addConnectionId(address: Address): ConnectionId
    fun removeConnectionIdForAddress(connectionId: ConnectionId)

    fun clearStore()
    fun getAddress(id: ConnectionId): Address?
    fun getHostConnectionId(): ConnectionId
    fun getConnectionIdForAddress(address: Address): ConnectionId?
    fun findMissingConnections(currentConnections: List<Address>): List<ConnectionId>
}