package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.MessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CardForExchangeChosenUsecase @Inject constructor(
    private val communicator: GameCommunicator,
    private val store: InGameStore,
    private val dwitchFactory: DwitchFactory
) {

    fun chooseCardForExchange(cards: Set<Card>): Completable {
        return Completable.fromAction {
            val localPlayerId = store.getLocalPlayerDwitchId()

            val gameStateUpdated =
                dwitchFactory.createDwitchEngine(store.getGameState()).chooseCardsForExchange(localPlayerId, cards)
            store.updateGameState(gameStateUpdated)

            val message = MessageFactory.createCardsForExchangeChosenMessage(localPlayerId, cards)
            communicator.sendMessageToHost(message)
        }
    }
}
