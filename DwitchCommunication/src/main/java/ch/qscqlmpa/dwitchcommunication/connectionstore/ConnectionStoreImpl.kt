package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import java.util.concurrent.atomic.AtomicLong


internal class ConnectionStoreImpl : ConnectionStore, ConnectionStoreInternal {

    private val hostConnectionId = ConnectionId(0)

    private val nextLocalConnectionId = AtomicLong(1)

    private val addressMap: MutableMap<ConnectionId, Address> = HashMap()
    private val addressReverseMap: MutableMap<Address, ConnectionId> = HashMap()
    private val playerDwitchIdMap: MutableMap<ConnectionId, PlayerDwitchId> = HashMap()
    private val playerDwitchIdReverseeMap: MutableMap<PlayerDwitchId, ConnectionId> = HashMap()

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

    override fun pairConnectionWithPlayer(connectionId: ConnectionId, playerDwitchId: PlayerDwitchId) {
        playerDwitchIdMap[connectionId] = playerDwitchId
        playerDwitchIdReverseeMap[playerDwitchId] = connectionId
    }

    override fun getHostConnectionId(): ConnectionId {
        return hostConnectionId
    }

    override fun getConnectionIdForAddress(address: Address): ConnectionId? {
        return addressReverseMap[address]
    }

    override fun getAddress(id: ConnectionId): Address? {
        return addressMap[id]
    }

    override fun getDwitchId(connectionId: ConnectionId): PlayerDwitchId? {
        return playerDwitchIdMap[connectionId]
    }

    override fun getConnectionId(playerDwitchId: PlayerDwitchId): ConnectionId? {
        return playerDwitchIdReverseeMap[playerDwitchId]
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