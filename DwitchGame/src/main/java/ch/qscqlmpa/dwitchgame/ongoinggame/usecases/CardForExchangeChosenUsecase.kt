package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CardForExchangeChosenUsecase @Inject constructor(
    private val communicator: GameCommunicator
) {

    fun chooseCardForExchange(cards: List<Card>): Completable {
        return Single.fromCallable {
            MessageFactory.createCardsForExchangeChoseMessage()
        }
        communicator.sendMessage().andThen()
    }
}