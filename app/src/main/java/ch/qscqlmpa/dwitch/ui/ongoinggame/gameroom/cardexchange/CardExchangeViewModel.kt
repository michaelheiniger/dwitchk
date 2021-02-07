package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardItem
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import io.reactivex.rxjava3.core.Scheduler
import timber.log.Timber
import javax.inject.Inject

class CardExchangeViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private lateinit var cardExchangeEvent: CardExchange
    private lateinit var selectableCards: Set<Card>

    private var cardsInHand: MutableList<Card> = mutableListOf()
    private var cardsChosen: MutableList<Card> = mutableListOf()

    private val commands = MutableLiveData<CardExchangeCommand>()
    private val submitControl = MutableLiveData(UiControlModel(enabled = false))
    private val cardInHandItems = MutableLiveData<List<CardItem>>(listOf())
    private val cardChosenItems = MutableLiveData<List<CardItem>>(listOf())

    init {
        initialize()
    }

    fun commands(): LiveData<CardExchangeCommand> {
        return commands
    }

    fun submitControl(): LiveData<UiControlModel> {
        return submitControl
    }

    fun cardsInHand(): LiveData<List<CardItem>> {
        return cardInHandItems
    }

    fun cardsChosen(): LiveData<List<CardItem>> {
        return cardChosenItems
    }

    fun cardInHandClicked(card: Card) {
        if (!selectableCards.contains(card)) {
            Timber.w("Card $card cannot be chosen because it has a too low value.")
            return
        }
        if (cardsChosen.size == cardExchangeEvent.numCardsToChoose) {
            Timber.w("No more card can be chosen: already ${cardsChosen.size} chosen.")
            return
        }
        val removalSuccessful = cardsInHand.remove(card)
        if (removalSuccessful) {
            cardsChosen.add(card)
            updateCardsInHandItems()
            updateCardChosenItems()
            updateExchangeCardButton()
        } else {
            throw IllegalArgumentException("Card $card is not in the hand !")
        }
    }

    fun cardChosenClicked(card: Card) {
        val removalSuccessful = cardsChosen.remove(card)
        if (removalSuccessful) {
            cardsInHand.add(card)
            updateCardsInHandItems()
            updateCardChosenItems()
            updateExchangeCardButton()
        } else {
            throw IllegalArgumentException("Card $card is not in the chosen cards !")
        }
    }

    fun confirmChoice() {
        Timber.v("confirmChoice()")
        disposableManager.add(
            facade.submitCardsForExchange(cardChosenItems.value!!.map(CardItem::card).toSet())
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Timber.i("Cards for exchange submitted successfully.")
                        commands.value = CardExchangeCommand.Close
                    },
                    { error -> Timber.e(error, "Error while submitting cards for exchange.") }
                ),
        )
    }

    private fun updateCardsInHandItems() {
        cardInHandItems.value = cardsInHand.map { c -> CardItem(c, isCardSelectable(c)) }
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

    private fun initialize() {
        disposableManager.add(
            facade.getCardExchangeInfo()
                .observeOn(uiScheduler)
                .subscribe(
                    { cardExchangeInfo ->
                        Timber.d("Card exchange info: $cardExchangeInfo")
                        cardExchangeEvent = cardExchangeInfo.cardExchange

                        Timber.d("Cards in hand: ${cardExchangeInfo.cardsInHand}")
                        cardsInHand = cardExchangeInfo.cardsInHand.toMutableList()
                        selectableCards = cardExchangeInfo.cardsInHand.filter(::isCardSelectable).toSet()
                        updateCardsInHandItems()
                    },
                    { error -> Timber.e(error, "Error while initializing card exchange.") }
                )
        )
    }

    private fun updateExchangeCardButton() {
        val enabled = cardsChosen.size == cardExchangeEvent.numCardsToChoose
        submitControl.value = UiControlModel(enabled = enabled)
    }
}
