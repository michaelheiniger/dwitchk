package ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards

class PlayCardEngine(
    private val cardsInHand: List<CardInfo>,
    private val lastCardPlayed: PlayedCards?
) {
    private val cardsMultiplicity = cardsInHand.groupBy { c -> c.card.name }.mapValues { (_, l) -> l.size }
    private val cardsSelected = mutableListOf<Card>()

    fun onCardClick(card: Card) {
        if (lastCardPlayed == null) { // No card on table, hence no restriction on the number of cards that can be played together
            require(cardsInHand.any { c -> c.card == card }) { "Card $card is not in the hand." }
            requireCardToHaveSameNameAsOtherSelectedCards(card)
        } else {
            requireCardToHaveSameNameAsOtherSelectedCards(card)
            requireCardToBeSelectable(card)
            requireNumberOfCardsSelectedToBeLessOrEqualToLastCardPlayedMultiplicity(card, lastCardPlayed.multiplicity)
        }
        if (cardsSelected.contains(card)) cardsSelected.remove(card) else cardsSelected.add(card)
    }

    fun getCardsInHand(): List<CardInfo> {
        return if (lastCardPlayed == null) {
            if (cardsSelected.isEmpty()) {
                cardsInHand.map { c -> CardInfo(c.card, selectable = c.selectable, selected = false) }
            } else {
                cardsInHand.map { cardInfo ->
                    val selectable = cardInfo.selectable && cardHasSameNameAsOtherSelectedCards(cardInfo)
                    CardInfo(cardInfo.card, selectable, cardIsAlreadySelected(cardInfo))
                }
            }
        } else {
            if (cardsSelected.isEmpty()) {
                cardsInHand.map { cardInfo ->
                    val currentCardMultiplicity = cardsMultiplicity[cardInfo.card.name] ?: 0
                    val selectable = cardInfo.selectable && currentCardMultiplicity >= lastCardPlayed.multiplicity
                    CardInfo(cardInfo.card, selectable, cardIsAlreadySelected(cardInfo))
                }
            } else {
                cardsInHand.map { cardInfo ->
                    val atLeastOneMoreCardCanBeSelected = cardsSelected.size < lastCardPlayed.multiplicity
                    val selectable = cardInfo.selectable &&
                            cardHasSameNameAsOtherSelectedCards(cardInfo) &&
                            (atLeastOneMoreCardCanBeSelected || cardIsAlreadySelected(cardInfo))
                    CardInfo(cardInfo.card, selectable, cardIsAlreadySelected(cardInfo))
                }
            }
        }
    }

    fun getSelectedCards(): PlayedCards {
        return PlayedCards(cardsSelected)
    }

    fun cardSelectionIsValid(): Boolean {
        return if (lastCardPlayed == null) cardsSelected.size >= 1
        else cardsSelected.size == lastCardPlayed.multiplicity
    }

    private fun requireCardToHaveSameNameAsOtherSelectedCards(card: Card) {
        require(cardsSelected.isEmpty() || cardsSelected.all { c -> c.name == card.name })
        { "Card $card is not selectable: name is different than already selected card(s)." }
    }

    private fun requireCardToBeSelectable(card: Card) {
        require(cardsInHand.find { c -> c.card == card && c.selectable } != null) { "Card $card is no selectable." }
    }

    private fun requireNumberOfCardsSelectedToBeLessOrEqualToLastCardPlayedMultiplicity(card: Card, cardMultiplicity: Int) {
        if (!cardsSelected.contains(card)) require(cardsSelected.size + 1 <= cardMultiplicity)
        { "Cannot select more cards than multiplicity of last card played" }
    }

    private fun cardHasSameNameAsOtherSelectedCards(cardInfo: CardInfo) = cardInfo.card.name == nameOfSelectedCards()

    private fun cardIsAlreadySelected(cardInfo: CardInfo) = cardsSelected.contains(cardInfo.card)

    private fun nameOfSelectedCards() = cardsSelected[0].name
}
