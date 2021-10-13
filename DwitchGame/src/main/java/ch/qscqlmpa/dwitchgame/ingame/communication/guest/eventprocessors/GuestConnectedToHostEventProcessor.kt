package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GuestConnectedToHostEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator,
    commStateRepository: GuestCommunicationStateRepository,
) : BaseGuestCommunicationEventProcessor(commStateRepository) {

    override fun process(event: ClientEvent.CommunicationEvent): Completable {

        event as ClientEvent.CommunicationEvent.ConnectedToServer

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
        }.andThen(super.process(event))
    }

    private fun guestIsAlreadyRegisteredAtHost(localPlayer: Player): Boolean {
        return localPlayer.dwitchId != DwitchPlayerId(0)
    }
}
