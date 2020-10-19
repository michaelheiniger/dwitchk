package ch.qscqlmpa.dwitch.ongoinggame.communication

import ch.qscqlmpa.dwitch.communication.Address
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject


@OngoingGameScope
class LocalConnectionIdStore @Inject constructor() {

    private val nextLocalConnectionId = AtomicLong(0)

    private val addressMap: MutableMap<LocalConnectionId, Address> = HashMap()
    private val addressReverseMap: MutableMap<Address, LocalConnectionId> = HashMap()
    private val playerInGameMap: MutableMap<LocalConnectionId, PlayerInGameId> = HashMap()

    fun addAddress(address: Address): LocalConnectionId {
        val localConnectionId = LocalConnectionId(nextLocalConnectionId.getAndIncrement())
        addressMap[localConnectionId] = address
        addressReverseMap[address] = localConnectionId
        return localConnectionId
    }

    fun addPlayerInGameId(localConnectionId: LocalConnectionId, playerInGameId: PlayerInGameId) {
        playerInGameMap[localConnectionId] = playerInGameId
    }

    fun removeLocalConnectionId(localConnectionId: LocalConnectionId) {
        val address = addressMap.remove(localConnectionId)
        addressReverseMap.remove(address)
        playerInGameMap.remove(localConnectionId)
    }

    fun getLocalConnectionIdForAddress(address: Address): LocalConnectionId? {
        return addressReverseMap[address]
    }

    fun getAddress(localId: LocalConnectionId): Address? {
        return addressMap[localId]
    }

    fun getInGameId(localId: LocalConnectionId): PlayerInGameId? {
        return playerInGameMap[localId]
    }
}