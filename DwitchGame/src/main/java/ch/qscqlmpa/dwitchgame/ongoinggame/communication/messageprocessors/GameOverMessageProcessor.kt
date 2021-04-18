package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEventRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GameOverMessageProcessor @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.fromAction {
            appEventRepository.notify(AppEvent.GameOverGuest)
            gameEventRepository.notify(GuestGameEvent.GameOver)
        }
    }
}
