package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameOverMessageProcessor @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val gameLifecycleEventRepository: GuestGameLifecycleEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.fromAction {
            gameEventRepository.notify(GuestGameEvent.GameOver)
            gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver)
        }
    }
}
