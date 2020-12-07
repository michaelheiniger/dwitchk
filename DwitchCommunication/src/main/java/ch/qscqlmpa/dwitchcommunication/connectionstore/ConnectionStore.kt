package ch.qscqlmpa.dwitchcommunication.connectionstore

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId


interface ConnectionStore {

    /**
     * Here, "Player" can either be the Host or a Guest. The Host does always have a connectionId.
     */
    fun pairConnectionWithPlayer(connectionId: ConnectionId, playerInGameId: PlayerInGameId)
    fun removeConnectionIdForInGameId(connectionId: ConnectionId)
    fun getInGameId(connectionId: ConnectionId): PlayerInGameId?
    fun getConnectionId(playerInGameId: PlayerInGameId): ConnectionId?
}