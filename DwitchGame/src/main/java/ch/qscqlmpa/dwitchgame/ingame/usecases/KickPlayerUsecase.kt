package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class KickPlayerUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: HostCommunicator,
    private val connectionStore: ConnectionStore,
    private val hostMessageFactory: HostMessageFactory
) {

    fun kickPlayer(player: PlayerWrUi): Completable {
        return Completable.fromAction {
            val playerFromStore = store.getPlayer(player.id)

            val connectionId = connectionStore.getConnectionId(playerFromStore.dwitchId)
            if (connectionId != null) {
                communicator.sendMessage(HostMessageFactory.createKickPlayerMessage(playerFromStore.dwitchId, connectionId))
            } else {
                Logger.warn { "Cannot send KickPlayerMessage: no connection ID found for player ${playerFromStore.dwitchId}" }
            }

            store.deletePlayers(listOf(player.id))
            communicator.sendMessage(hostMessageFactory.createWaitingRoomStateUpdateMessage())
        }
    }
}