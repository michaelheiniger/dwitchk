package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import javax.inject.Inject

internal class GameOverMessageProcessor @Inject constructor(
    private val serviceManager: ServiceManager,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {
        return Completable.fromAction {
            serviceManager.stopGuestService()
            gameEventRepository.notify(GuestGameEvent.GameOver)
        }
    }
}