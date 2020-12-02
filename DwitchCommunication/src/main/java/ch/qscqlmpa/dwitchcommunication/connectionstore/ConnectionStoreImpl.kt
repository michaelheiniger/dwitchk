package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import java.util.concurrent.atomic.AtomicLong


internal class ConnectionStoreImpl : ConnectionStore {

    private val nextLocalConnectionId = AtomicLong(0)

    private val addressMap: MutableMap<LocalConnectionId, Address> = HashMap()
    private val addressReverseMap: MutableMap<Address, LocalConnectionId> = HashMap()
    private val playerInGameMap: MutableMap<LocalConnectionId, PlayerInGameId> = HashMap()

    override fun addConnectionId(address: Address): LocalConnectionId {
        val localConnectionId = LocalConnectionId(nextLocalConnectionId.getAndIncrement())
        addressMap[localConnectionId] = address
        addressReverseMap[address] = localConnectionId
        return localConnectionId
    }

    override fun removeConnectionId(localConnectionId: LocalConnectionId) {
        val address = addressMap.remove(localConnectionId)
        addressReverseMap.remove(address)
        playerInGameMap.remove(localConnectionId)
    }

    override fun mapPlayerIdToConnectionId(localConnectionId: LocalConnectionId, playerInGameId: PlayerInGameId) {
        playerInGameMap[localConnectionId] = playerInGameId
    }

    override fun getLocalConnectionIdForAddress(address: Address): LocalConnectionId? {
        return addressReverseMap[address]
    }

    override fun getAddress(localId: LocalConnectionId): Address? {
        return addressMap[localId]
    }

    override fun getInGameId(localId: LocalConnectionId): PlayerInGameId? {
        return playerInGameMap[localId]
    }

    override fun findMissingConnections(currentConnections: List<Address>): List<LocalConnectionId> {
        return addressReverseMap.filterKeys { address -> !currentConnections.contains(address) }
            .values.toList()
    }
}