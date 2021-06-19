package ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard.GameRoomScreenBuilder
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardValueDescComparator
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import org.tinylog.kotlin.Logger

class CardExchangeScreenBuilder constructor(
    cardExchangeInfo: CardExchangeInfo
) : GameRoomScreenBuilder {
    private var cardExchangeInfo: CardExchangeInfo
    private val cardExchangeEngine: CardExchangeEngine

    val selectedCards get() = cardExchangeEngine.getCardsToExchange()
    override val screen get() = GameRoomScreen.CardExchange(cardExchangeEngine.getCardExchangeState())

    init {
        Logger.debug { "Create new ScreenBuilder ($this)" }
        val sortedCardsInHand = cardExchangeInfo.cardsInHand.sortedWith(CardValueDescComparator())
        this.cardExchangeInfo = cardExchangeInfo.copy(cardsInHand = sortedCardsInHand)
        cardExchangeEngine = CardExchangeEngine(this.cardExchangeInfo)
    }

    fun onCardClick(card: Card): GameRoomScreen.CardExchange {
        cardExchangeEngine.onCardToExchangeClick(card)
        return GameRoomScreen.CardExchange(cardExchangeEngine.getCardExchangeState())
    }
}