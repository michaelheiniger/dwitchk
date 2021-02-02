package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CardForExchangeChosenUsecase @Inject constructor(
    private val communicator: GameCommunicator,
    private val inGameStore: InGameStore
) {

    fun chooseCardForExchange(cards: Set<Card>): Completable {
        return Completable.fromAction {
            val message = MessageFactory.createCardsForExchangeChosenMessage(inGameStore.getLocalPlayerDwitchId(), cards)
            communicator.sendMessageToHost(message)
            inGameStore.deleteCardExchangeEvent()
        }
    }
}