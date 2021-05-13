package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardValueDescComparator
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameAction
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class CardExchangeManager @Inject constructor(private val facade: GameFacade) {
    private val disposableManager = DisposableManager()
    private val relay = PublishRelay.create<GameRoomScreen>()
    private lateinit var cardExchangeInfo: CardExchangeInfo
    private var cardExchangeEngine: CardExchangeEngine? = null

    init {
        disposableManager.add(
            facade.observeGameData()
                .subscribe { data ->
                    when (data) {
                        is DwitchState.CardExchange -> setupAndEmitInitialScreen(data.info)
                        DwitchState.CardExchangeOnGoing -> relay.accept(GameRoomScreen.CardExchangeOnGoing)
                        else -> {
                            // Nothing to do
                        }
                    }
                }
        )
    }

    fun observeScreen(): Observable<GameRoomScreen> {
        return relay.doOnDispose { disposableManager.disposeAndReset() }
    }

    fun onCardToExchangeClick(card: Card) {
        cardExchangeEngine!!.onCardToExchangeClick(card)
        relay.accept(GameRoomScreen.CardExchange(cardExchangeEngine!!.getCardExchangeState()))
    }

    fun confirmExchange(): Completable {
        return facade.performAction(GameAction.SubmitCardsForExchange(cardExchangeEngine!!.getCardsToExchange()))
    }

    private fun setupAndEmitInitialScreen(info: CardExchangeInfo) {
        val sortedCardsInHand = info.cardsInHand.sortedWith(CardValueDescComparator())
        cardExchangeInfo = info.copy(cardsInHand = sortedCardsInHand)
        cardExchangeEngine = CardExchangeEngine(cardExchangeInfo)
        relay.accept(GameRoomScreen.CardExchange(cardExchangeEngine!!.getCardExchangeState()))
    }
}