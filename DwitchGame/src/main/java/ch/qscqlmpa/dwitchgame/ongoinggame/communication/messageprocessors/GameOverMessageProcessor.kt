package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEventRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameOverMessageProcessor @Inject constructor(
    private val gameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.fromAction {
            gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameOver)
            gameEventRepository.notify(GuestGameEvent.GameOver)
        }
    }
}
