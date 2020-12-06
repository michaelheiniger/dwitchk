package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectedToHostEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator,
    private val commStateRepository: GuestCommunicationStateRepository,
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {

        Timber.d("Process GuestConnectedToHostEvent")

        event as ClientCommunicationEvent.ConnectedToHost

        return Single.fromCallable {
            val game = store.getGame()
            val localPlayer = store.getLocalPlayer()

            if (guestIsAlreadyRegisteredAtHost(localPlayer)) {
                Timber.d("Send RejoinGameMessage with in-game ID ${localPlayer.inGameId}")
                GuestMessageFactory.createRejoinGameMessage(game.gameCommonId, localPlayer.inGameId)
            } else {
                Timber.d("Send JoinGameMessage")
                GuestMessageFactory.createJoinGameMessage(localPlayer.name)
            }
        }
            .flatMapCompletable(communicator::sendMessageToHost)
            .doOnComplete { commStateRepository.notify(GuestCommunicationState.Connected) }
    }

    private fun guestIsAlreadyRegisteredAtHost(localPlayer: Player): Boolean {
        return localPlayer.inGameId != PlayerInGameId(0)
    }
}