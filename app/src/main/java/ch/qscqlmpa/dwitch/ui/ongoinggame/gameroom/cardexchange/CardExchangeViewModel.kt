package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardItem
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerDashboardFacade
import ch.qscqlmpa.dwitchmodel.game.DwitchEvent
import timber.log.Timber
import javax.inject.Inject

class CardExchangeViewModel @Inject constructor(
    private val facade: PlayerDashboardFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private lateinit var cardExchangeEvent: DwitchEvent.CardExchange

    private var cardsInHand: MutableList<Card> = mutableListOf()
    private var cardsChosen: MutableList<Card> = mutableListOf()

    private val cardInHandItems = MutableLiveData<List<CardItem>>()
    private val cardChosenItems = MutableLiveData<List<CardItem>>()

    init {
        getCardsInHand()
        getCardExchangeEvent()
    }

    fun cardsInHand(): LiveData<List<CardItem>> {
        return cardInHandItems
    }

    fun cardsChosen(): LiveData<List<CardItem>> {
        return cardChosenItems
    }

    fun cardInHandClicked(card: Card) {
        val removalSuccessful = cardsInHand.remove(card)
        if (removalSuccessful) {
            cardsChosen.add(card)
            updateCardsInHandItems(card)
            updateCardChosenItems()
        } else {
            throw IllegalArgumentException("Card $card is not in the hand !")
        }
    }

    fun cardChosenClicked(card: Card) {
        val removalSuccessful = cardsChosen.remove(card)
        if (removalSuccessful) {
            cardsInHand.add(card)
            updateCardsInHandItems(card)
            updateCardChosenItems()
        } else {
            throw IllegalArgumentException("Card $card is not in the chose cards !")
        }
    }

    fun confirmChoice() {
        Timber.v("confirmChoice()")
        facade.cardForExchangeChosen()
    }

    private fun updateCardsInHandItems(card: Card) {
        cardInHandItems.value = cardsInHand.map { c -> CardItem(c, isCardSelectable(card)) }
    }

    private fun updateCardChosenItems() {
        cardChosenItems.value = cardsChosen.map { c -> CardItem(c, true) }
    }

    private fun isCardSelectable(card: Card): Boolean {
        val allowedCardValues = cardExchangeEvent.allowedCardValues.toMutableList()
        cardsChosen.forEach { c -> allowedCardValues.remove(c.name) }
        val cardHasAllowedValue = allowedCardValues.contains(card.name)

        Timber.v("Is card $card selectable ? ${card.value()} is in $allowedCardValues : $cardHasAllowedValue")
        return cardHasAllowedValue
    }

    private fun getCardsInHand() {
        disposableManager.add(
            facade.getDashboard()
                .map { dashboard -> dashboard.cardsInHand }
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .doOnError { error -> Timber.e(error, "Error while observing player dashboard.") }
                .subscribe(
                    { cards -> cardsInHand = cards.toMutableList()},
                    { error -> Timber.e(error, "Error while fetching cards in hand.") }
                ),
        )
    }

    private fun getCardExchangeEvent() {
            facade.observeCardExchangeEvents()
                .take(1)
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    { event -> cardExchangeEvent = event},
                    { error -> Timber.e(error, "Error while observing card exchange.") }
                )
    }
}
