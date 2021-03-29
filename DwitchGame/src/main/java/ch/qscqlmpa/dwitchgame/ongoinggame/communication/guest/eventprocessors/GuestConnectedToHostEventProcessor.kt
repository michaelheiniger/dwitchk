package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GuestConnectedToHostEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator,
    private val commStateRepository: GuestCommunicationStateRepository,
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {

        Logger.debug { "Process GuestConnectedToHostEvent" }

        event as ClientCommunicationEvent.ConnectedToHost

        return Completable.fromAction {
            val game = store.getGame()
            val localPlayer = store.getLocalPlayer()

            val message = if (guestIsAlreadyRegisteredAtHost(localPlayer)) {
                Logger.debug { "Send RejoinGameMessage with in-game ID ${localPlayer.dwitchId}" }
                GuestMessageFactory.createRejoinGameMessage(game.gameCommonId, localPlayer.dwitchId)
            } else {
                Logger.debug { "Send JoinGameMessage" }
                GuestMessageFactory.createJoinGameMessage(localPlayer.name)
            }

            communicator.sendMessageToHost(message)
            commStateRepository.updateState(GuestCommunicationState.Connected)
        }
    }

    private fun guestIsAlreadyRegisteredAtHost(localPlayer: Player): Boolean {
        return localPlayer.dwitchId != PlayerDwitchId(0)
    }
}
