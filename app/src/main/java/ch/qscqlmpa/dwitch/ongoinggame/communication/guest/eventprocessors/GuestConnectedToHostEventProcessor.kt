package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ConnectedToHost
import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectedToHostEventProcessor @Inject constructor(private val store: InGameStore,
                                                                      private val communicator: GuestCommunicator
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {

        Timber.d("Process ConnectedToHostEvent")

        event as ConnectedToHost

        return Single.fromCallable {

            val game = store.getGame()
            val localPlayer = store.getLocalPlayer()

            if (game.gameCommonId == 0L) { // Means that local player (a guest) is not registered at the host yet
                Timber.d("Send JoinGameMessage")
                return@fromCallable GuestMessageFactory.createJoinGameMessage(localPlayer.name)
            } else {
                Timber.d("Send RejoinGameMessage")
                return@fromCallable GuestMessageFactory.createRejoinGameMessage(localPlayer.inGameId)
            }
        }.flatMapCompletable(communicator::sendMessage)
    }
}