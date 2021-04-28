package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

interface ConnectionStore {

    /**
     * Here, "Player" can either be the Host or a Guest. The Host does always have a connectionId.
     */
    fun pairConnectionWithPlayer(connectionId: ConnectionId, dwitchPlayerId: DwitchPlayerId)
    fun removeConnectionIdForDwitchId(connectionId: ConnectionId)
    fun getDwitchId(connectionId: ConnectionId): DwitchPlayerId?
    fun getConnectionId(dwitchPlayerId: DwitchPlayerId): ConnectionId?

    companion object {

        val hostConnectionId = ConnectionId(0) // Keep in sync with ConnectionStoreInternal.hostConnectionId

        /**
         * Range of connection IDs reserved for computer players.
         */
        val computerConnectionIdRange = 1..10
    }
}
