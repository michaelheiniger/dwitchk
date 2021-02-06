package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

interface ConnectionStore {

    /**
     * Here, "Player" can either be the Host or a Guest. The Host does always have a connectionId.
     */
    fun pairConnectionWithPlayer(connectionId: ConnectionId, playerDwitchId: PlayerDwitchId)
    fun removeConnectionIdForDwitchId(connectionId: ConnectionId)
    fun getDwitchId(connectionId: ConnectionId): PlayerDwitchId?
    fun getConnectionId(playerDwitchId: PlayerDwitchId): ConnectionId?
}
