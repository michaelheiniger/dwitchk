package ch.qscqlmpa.dwitchstore.ingamestore.model

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange

data class CardExchangeInfo(val cardExchange: DwitchCardExchange, val cardsInHand: List<Card>)
