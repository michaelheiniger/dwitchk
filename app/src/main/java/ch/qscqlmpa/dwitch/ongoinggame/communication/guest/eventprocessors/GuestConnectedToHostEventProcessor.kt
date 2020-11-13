package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectedToHostEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator,
    private val commEventRepository: GuestCommunicationEventRepository,
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
            .flatMapCompletable(communicator::sendMessage)
            .doOnComplete { commEventRepository.notify(GuestCommunicationState.Connected) }
    }

    private fun guestIsAlreadyRegisteredAtHost(localPlayer: Player): Boolean {
        return localPlayer.inGameId != PlayerInGameId(0)
    }
}