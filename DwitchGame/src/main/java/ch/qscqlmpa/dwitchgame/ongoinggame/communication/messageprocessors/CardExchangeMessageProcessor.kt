package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.game.DwitchEvent
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject

internal class CardExchangeMessageProcessor @Inject constructor(
    private val store: InGameStore
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.CardExchangeMessage

        val cardExchange = msg.cardExchange

        return Completable.fromAction {
            if (msg.playerInGameId != store.getLocalPlayerInGameId()) {
                Timber.w("Received CardExchangeMessage intended for another player: $msg")
            } else {
                store.insertDwitchEvent(
                    DwitchEvent.CardExchange(
                        numCardsToExchange = cardExchange.numCardsToChoose,
                        allowedCardValues = cardExchange.allowedCardValues,
                        creationDate = DateTime.now()
                    )
                )
            }
        }
    }
}