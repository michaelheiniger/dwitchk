package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId


interface ConnectionStore {

    fun addConnectionId(address: Address): LocalConnectionId
    fun removeConnectionId(localConnectionId: LocalConnectionId)
    fun mapPlayerIdToConnectionId(localConnectionId: LocalConnectionId, playerInGameId: PlayerInGameId)
    fun getLocalConnectionIdForAddress(address: Address): LocalConnectionId?
    fun getAddress(localId: LocalConnectionId): Address?
    fun getInGameId(localId: LocalConnectionId): PlayerInGameId?
    fun findMissingConnections(currentConnections: List<Address>): List<LocalConnectionId>
}