package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import java.util.concurrent.atomic.AtomicLong


internal class ConnectionStoreImpl : ConnectionStore {

    private val nextConnectionId = AtomicLong(0)

    private val addressMap: MutableMap<ConnectionId, Address> = HashMap()
    private val addressReverseMap: MutableMap<Address, ConnectionId> = HashMap()
    private val playerInGameMap: MutableMap<ConnectionId, PlayerInGameId> = HashMap()
    private val playerInGameReverseMap: MutableMap<PlayerInGameId, ConnectionId> = HashMap()

    override fun addConnectionId(address: Address): ConnectionId {
        val connectionId = ConnectionId(nextConnectionId.getAndIncrement())
        addressMap[connectionId] = address
        addressReverseMap[address] = connectionId
        return connectionId
    }

    override fun removeConnectionId(connectionId: ConnectionId) {
        val address = addressMap.remove(connectionId)
        addressReverseMap.remove(address)
        val playerInGameId = playerInGameMap.remove(connectionId)
        playerInGameReverseMap.remove(playerInGameId)
    }

    override fun mapPlayerIdToConnectionId(connectionId: ConnectionId, playerInGameId: PlayerInGameId) {
        playerInGameMap[connectionId] = playerInGameId
        playerInGameReverseMap[playerInGameId] = connectionId
    }

    override fun getConnectionIdForAddress(address: Address): ConnectionId? {
        return addressReverseMap[address]
    }

    override fun getConnectionIdForIngameId(inGameId: PlayerInGameId): ConnectionId? {
        return playerInGameReverseMap[inGameId]
    }

    override fun getAddress(id: ConnectionId): Address? {
        return addressMap[id]
    }

    override fun getInGameId(id: ConnectionId): PlayerInGameId? {
        return playerInGameMap[id]
    }

    override fun findMissingConnections(currentConnections: List<Address>): List<ConnectionId> {
        return addressReverseMap.filterKeys { address -> !currentConnections.contains(address) }
            .values.toList()
    }
}