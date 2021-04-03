package ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class CardExchangeViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private lateinit var cardExchangeEvent: CardExchange
    private lateinit var selectableCards: Set<Card>

    private val cardsInHand: MutableList<Card> = mutableListOf()
    private val cardsToExchange: MutableList<Card> = mutableListOf()

    private val _commands = MutableLiveData<CardExchangeCommand>()
    private val _exchangeControlEnabled = MutableLiveData(false)
    private val _cardInHandItems = MutableLiveData<List<CardItem>>(listOf())
    private val _cardsToExchangeItems = MutableLiveData<List<CardItem>>(listOf())

    val commands get(): LiveData<CardExchangeCommand> = _commands
    val exchangeControlEnabled get(): LiveData<Boolean> = _exchangeControlEnabled
    val cardsInHandItems get(): LiveData<List<CardItem>> = _cardInHandItems
    val cardsToExchangeItems get(): LiveData<List<CardItem>> = _cardsToExchangeItems

    init {
        loadCardExchangeInfo()
    }

    fun addCardToExchange(card: Card) {
        if (!selectableCards.contains(card)) {
            Logger.warn { "Card $card cannot be chosen because it has a too low value." }
            return
        }
        if (cardsToExchange.size == cardExchangeEvent.numCardsToChoose) {
            Logger.warn { "No more card can be chosen: already ${cardsToExchange.size} chosen." }
            return
        }
        val removalSuccessful = cardsInHand.remove(card)
        if (removalSuccessful) {
            cardsToExchange.add(card)
            updateCardsInHandItems()
            updateCardChosenItems()
            updateExchangeControl()
        } else {
            throw IllegalArgumentException("Card $card is not in the hand !")
        }
    }

    fun removeCardFromExchange(card: Card) {
        val removalSuccessful = cardsToExchange.remove(card)
        if (removalSuccessful) {
            cardsInHand.add(card)
            updateCardsInHandItems()
            updateCardChosenItems()
            updateExchangeControl()
        } else {
            throw IllegalArgumentException("Card $card is not in the chosen cards !")
        }
    }

    fun confirmExchange() {
        Logger.trace { "confirmChoice()" }
        disposableManager.add(
            facade.submitCardsForExchange(_cardsToExchangeItems.value!!.map(CardItem::card).toSet())
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Cards for exchange submitted successfully." }
                        _commands.value = CardExchangeCommand.Close
                    },
                    { error -> Logger.error(error) { "Error while submitting cards for exchange." } }
                ),
        )
    }

    private fun loadCardExchangeInfo() {
        disposableManager.add(
            facade.getCardExchangeInfo()
                .observeOn(uiScheduler)
                .subscribe(
                    { cardExchangeInfo ->
                        Logger.debug { "Card exchange info: " }
                        cardExchangeEvent = cardExchangeInfo.cardExchange

                        Logger.debug { "Cards in hand: ${cardExchangeInfo.cardsInHand}" }
                        cardsInHand.addAll(cardExchangeInfo.cardsInHand)
                        selectableCards = cardExchangeInfo.cardsInHand.filter(::isCardSelectable).toSet()
                        updateCardsInHandItems()
                    },
                    { error -> Logger.error(error) { "Error while initializing card exchange." } }
                )
        )
    }

    private fun updateCardsInHandItems() {
        _cardInHandItems.value = cardsInHand.map { c -> CardItem(c, isCardSelectable(c)) }
    }

    private fun updateCardChosenItems() {
        _cardsToExchangeItems.value = cardsToExchange.map { c -> CardItem(c, true) }
    }

    private fun isCardSelectable(card: Card): Boolean {
        val allowedCardValues = cardExchangeEvent.allowedCardValues.toMutableList()
        cardsToExchange.forEach { c -> allowedCardValues.remove(c.name) }
        val cardHasAllowedValue = allowedCardValues.contains(card.name)

        Logger.trace { "Is card $card selectable ? ${card.value()} is in $allowedCardValues : $cardHasAllowedValue" }
        return cardHasAllowedValue
    }

    private fun updateExchangeControl() {
        _exchangeControlEnabled.value = cardsToExchange.size == cardExchangeEvent.numCardsToChoose
    }
}
