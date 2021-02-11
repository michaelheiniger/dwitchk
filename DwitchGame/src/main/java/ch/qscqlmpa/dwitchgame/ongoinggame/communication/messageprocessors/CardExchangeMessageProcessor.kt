package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import javax.inject.Inject

internal class CardExchangeMessageProcessor @Inject constructor(
    private val store: InGameStore
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.CardExchangeMessage

        val cardExchange = msg.cardExchange

        return Completable.fromAction {
            if (cardExchange.playerId != store.getLocalPlayerDwitchId()) {
                logger.warn { "Received CardExchangeMessage intended for another player: $msg" }
            } else {
                logger.info { "Received CardExchangeMessage: $msg" }
                store.addCardExchangeEvent(cardExchange)
            }
        }
    }

    companion object : KLogging()
}
