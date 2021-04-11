package ch.qscqlmpa.dwitchengine.model.info

import ch.qscqlmpa.dwitchengine.model.card.Card

data class DwitchCardInfo(val card: Card, val selectable: Boolean = true)
