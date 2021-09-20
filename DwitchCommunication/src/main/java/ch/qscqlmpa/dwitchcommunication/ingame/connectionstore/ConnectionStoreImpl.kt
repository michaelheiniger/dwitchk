package ch.qscqlmpa.dwitchcommunication.ingame.connectionstore

import ch.qscqlmpa.dwitchcommunication.ingame.Address
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import java.util.concurrent.atomic.AtomicLong

internal class ConnectionStoreImpl : ConnectionStore, ConnectionStoreInternal {

    private val nextLocalConnectionId = AtomicLong(ConnectionStore.computerConnectionIdRange.last + 1L)

    private val addressMap: MutableMap<ConnectionId, Address> = HashMap()
    private val addressReverseMap: MutableMap<Address, ConnectionId> = HashMap()
    private val playerDwitchIdMap: MutableMap<ConnectionId, DwitchPlayerId> = HashMap()
    private val playerDwitchIdReverseeMap: MutableMap<DwitchPlayerId, ConnectionId> = HashMap()

    override fun addConnectionId(address: Address): ConnectionId {
        val localConnectionId = getNextLocalConnectionId()
        addressMap[localConnectionId] = address
        addressReverseMap[address] = localConnectionId
        return localConnectionId
    }

    override fun removeConnectionIdForAddress(connectionId: ConnectionId) {
        val address = addressMap.remove(connectionId)
        addressReverseMap.remove(address)
    }

    override fun removeConnectionIdForDwitchId(connectionId: ConnectionId) {
        val playerDwitchId = playerDwitchIdMap.remove(connectionId)
        if (playerDwitchId != null) {
            playerDwitchIdReverseeMap.remove(playerDwitchId)
        }
    }

    override fun pairConnectionWithPlayer(connectionId: ConnectionId, dwitchPlayerId: DwitchPlayerId) {
        playerDwitchIdMap[connectionId] = dwitchPlayerId
        playerDwitchIdReverseeMap[dwitchPlayerId] = connectionId
    }

    override fun getConnectionIdForAddress(address: Address): ConnectionId? {
        return addressReverseMap[address]
    }

    override fun getAddress(id: ConnectionId): Address? {
        return addressMap[id]
    }

    override fun getDwitchId(connectionId: ConnectionId): DwitchPlayerId? {
        return playerDwitchIdMap[connectionId]
    }

    override fun getConnectionId(dwitchPlayerId: DwitchPlayerId): ConnectionId? {
        return playerDwitchIdReverseeMap[dwitchPlayerId]
    }

    override fun findMissingConnections(currentConnections: List<Address>): List<ConnectionId> {
        return addressReverseMap.filterKeys { address -> !currentConnections.contains(address) }
            .values.toList()
    }

    override fun clearStore() {
        addressMap.clear()
        addressReverseMap.clear()
        playerDwitchIdMap.clear()
        playerDwitchIdReverseeMap.clear()
    }

    private fun getNextLocalConnectionId() = ConnectionId(nextLocalConnectionId.getAndIncrement())
}
