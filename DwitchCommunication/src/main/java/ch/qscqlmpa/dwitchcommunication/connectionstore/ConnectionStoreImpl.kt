package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import java.util.concurrent.atomic.AtomicLong


internal class ConnectionStoreImpl : ConnectionStore, ConnectionStoreInternal {

    private val nextLocalConnectionId = AtomicLong(0)

    private val addressMap: MutableMap<ConnectionId, Address> = HashMap()
    private val addressReverseMap: MutableMap<Address, ConnectionId> = HashMap()
    private val playerInGameIdMap: MutableMap<ConnectionId, PlayerInGameId> = HashMap()
    private val playerInGameIdReverseeMap: MutableMap<PlayerInGameId, ConnectionId> = HashMap()

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

    override fun removeConnectionIdForInGameId(connectionId: ConnectionId) {
        val playerInGameId = playerInGameIdMap.remove(connectionId)
        if (playerInGameId != null) {
            playerInGameIdReverseeMap.remove(playerInGameId)
        }
    }

    override fun pairConnectionWithPlayer(connectionId: ConnectionId, playerInGameId: PlayerInGameId) {
        playerInGameIdMap[connectionId] = playerInGameId
        playerInGameIdReverseeMap[playerInGameId] = connectionId
    }

    override fun getConnectionIdForAddress(address: Address): ConnectionId? {
        return addressReverseMap[address]
    }

    override fun getAddress(id: ConnectionId): Address? {
        return addressMap[id]
    }

    override fun getInGameId(connectionId: ConnectionId): PlayerInGameId? {
        return playerInGameIdMap[connectionId]
    }

    override fun getConnectionId(playerInGameId: PlayerInGameId): ConnectionId? {
        return playerInGameIdReverseeMap[playerInGameId]
    }

    override fun findMissingConnections(currentConnections: List<Address>): List<ConnectionId> {
        return addressReverseMap.filterKeys { address -> !currentConnections.contains(address) }
            .values.toList()
    }

    override fun clearStore() {
        addressMap.clear()
        addressReverseMap.clear()
        playerInGameIdMap.clear()
        playerInGameIdReverseeMap.clear()
    }

    private fun getNextLocalConnectionId() = ConnectionId(nextLocalConnectionId.getAndIncrement())
}