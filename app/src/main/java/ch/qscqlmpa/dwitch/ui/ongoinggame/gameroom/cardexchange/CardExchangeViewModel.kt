package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard.CardItem
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject

class CardExchangeViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private lateinit var cardExchangeEvent: CardExchange

    private var cardsInHand: MutableList<Card> = mutableListOf()
    private var cardsChosen: MutableList<Card> = mutableListOf()

    private val commands = MutableLiveData<CardExchangeCommand>()
    private val cardInHandItems = MutableLiveData<List<CardItem>>(listOf())
    private val cardChosenItems = MutableLiveData<List<CardItem>>(listOf())

    init {
        initialize()
    }

    fun commands(): LiveData<CardExchangeCommand> {
        return commands
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
            updateCardsInHandItems()
            updateCardChosenItems()
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
        } else {
            throw IllegalArgumentException("Card $card is not in the chose cards !")
        }
    }

    fun confirmChoice() {
        Timber.v("confirmChoice()")
        disposableManager.add(
            facade.submitCardsForExchange(cardChosenItems.value!!.map(CardItem::card).toSet())
                .subscribeOn(schedulerFactory.io())
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    {
                        Timber.i("Cards for exchange submitted successfully.")
                        commands.value = CardExchangeCommand.Close
                    },
                    { error -> Timber.e(error, "Error while submitting cards for excĥange.") }
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
            Single.zip(
                getCardExchangeEvents(),
                getDashboard(),
                { event, cards ->
                    Timber.d("Card exchange event: $event")
                    cardExchangeEvent = event

                    Timber.d("Cards in hand: $cards")
                    cardsInHand = cards.toMutableList()
                    updateCardsInHandItems()
                }
            ).subscribe(
                {},
                { error -> Timber.e(error, "Error while initializing card exchange.") }
            )
        )
    }

    private fun getDashboard() = facade.getDashboard()
        .map { dashboard -> dashboard.localPlayerInfo.cardsInHand }
        .subscribeOn(schedulerFactory.io())
        .observeOn(schedulerFactory.ui())

    private fun getCardExchangeEvents() = facade.getCardExchangeEvent()
        .subscribeOn(schedulerFactory.io())
        .observeOn(schedulerFactory.ui())
}
