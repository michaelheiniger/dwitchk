package ch.qscqlmpa.dwitchstore.ingamestore.model

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange

data class CardExchangeInfo(val cardExchange: CardExchange, val cardsInHand: List<Card>)