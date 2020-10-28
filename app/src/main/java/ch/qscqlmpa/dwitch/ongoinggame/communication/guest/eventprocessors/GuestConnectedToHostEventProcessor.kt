package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectedToHostEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val communicator: GuestCommunicator
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {
        return Single.fromCallable {
            val localPlayer = store.getLocalPlayer()
            Timber.d("Send JoinGameMessage")
            GuestMessageFactory.createJoinGameMessage(localPlayer.name)
        }.flatMapCompletable(communicator::sendMessage)
    }
}