package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CardForExchangeChosenUsecase @Inject constructor(
    private val communicator: GameCommunicator,
    private val store: InGameStore,
    private val dwitchEngineFactory: DwitchEngineFactory
) {

    fun chooseCardForExchange(cards: Set<Card>): Completable {
        return Completable.fromAction {
            val localPlayerId = store.getLocalPlayerDwitchId()
            val message = MessageFactory.createCardsForExchangeChosenMessage(localPlayerId, cards)
            communicator.sendMessageToHost(message)
            val gameStateUpdated = dwitchEngineFactory.create(store.getGameState()).chooseCardsForExchange(localPlayerId, cards)
            store.updateGameState(gameStateUpdated)
        }
    }
}
