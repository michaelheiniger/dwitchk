package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

//TODO Write tests
internal class KickPlayerMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val gameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.fromCallable {
            if (store.gameIsNew()) {
                store.deleteGame()
            }
            gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.KickedOffGame)
            gameEventRepository.notify(GuestGameEvent.KickedOffGame)
        }
    }
}
