package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.usecases.ResumeComputerPlayersUsecase
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class HostListeningForConnectionsEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val connectionStore: ConnectionStore,
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val resumeComputerPlayersUsecase: ResumeComputerPlayersUsecase
) : HostCommunicationEventProcessor {

    override fun process(event: ServerEvent.CommunicationEvent): Completable {

        event as ServerEvent.CommunicationEvent.ListeningForConnections

        return Completable.fromAction {
            Logger.info { "Listening for connections..." }
            val hostDwitchId = store.getLocalPlayerDwitchId()
            Logger.debug { "pair host connection ID ${ConnectionStore.hostConnectionId} to its Dwitch id: $hostDwitchId" }
            connectionStore.pairConnectionWithPlayer(ConnectionStore.hostConnectionId, hostDwitchId)
            communicationStateRepository.updateState(HostCommunicationState.Open)

            if (store.gameIsNotNew()) {
                resumeComputerPlayersUsecase.resumeComputerPlayers()
            }
        }
    }
}
