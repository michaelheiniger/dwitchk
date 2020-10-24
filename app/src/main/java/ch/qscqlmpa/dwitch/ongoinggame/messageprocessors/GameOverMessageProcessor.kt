package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable
import javax.inject.Inject

internal class GameOverMessageProcessor @Inject constructor(
    private val gameEventRepository: GameEventRepository,
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {
        return Completable.fromAction { gameEventRepository.notifyOfEvent(GameEvent.GameOver) }
    }
}