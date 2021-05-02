package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class KickPlayerMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val appEventRepository: AppEventRepository,
    private val gameEventRepository: GuestGameEventRepository

) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.fromCallable {
            if (store.gameIsNew()) {
                store.deleteGame()
            }
            appEventRepository.notify(AppEvent.GameLeft)
            gameEventRepository.notify(GuestGameEvent.KickedOffGame)
        }
    }
}
