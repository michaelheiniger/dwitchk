package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange.CardExchangeStateEngine
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class PlayerDashboardViewModel @Inject constructor(
    private val facade: GameFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private var cardExchangeStateEngine: CardExchangeStateEngine? = null

    private val _screen = MutableLiveData<GameRoomScreen>()
    val screen get(): LiveData<GameRoomScreen> = _screen

    fun playCard(cardPlayed: Card) {
        performOperation("Card $cardPlayed played successfully.", "Error while playing card $cardPlayed.") {
            facade.playCard(cardPlayed)
        }
    }

    fun pickCard() {
        performOperation("Card picked successfully.", "Error while picking card.") { facade.pickCard() }
    }

    fun passTurn() {
        performOperation("Turn passed successfully.", "Error while passing turn.") { facade.passTurn() }
    }

    fun addCardToExchange(card: Card) {
        cardExchangeStateEngine!!.addCardToExchange(card)
        _screen.value = GameRoomScreen.CardExchange(cardExchangeStateEngine!!.getCardExchangeState())
    }

    fun removeCardFromExchange(card: Card) {
        cardExchangeStateEngine!!.removeCardFromExchange(card)
        _screen.value = GameRoomScreen.CardExchange(cardExchangeStateEngine!!.getCardExchangeState())
    }

    fun confirmExchange() {
        Logger.trace { "confirmExchange()" }
        val cardsToExchange = cardExchangeStateEngine!!.getCardExchangeState().cardsToExchange.map(DwitchCardInfo::card)
        disposableManager.add(
            facade.submitCardsForExchange(cardsToExchange.toSet())
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Cards for exchange submitted successfully." }
                        cardExchangeStateEngine = null
                    },
                    { error -> Logger.error(error) { "Error while submitting cards for exchange." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        observeGameState()
    }

    override fun onStop() {
        super.onStop()
        disposableManager.disposeAndReset()
    }

    private fun observeGameState() {
        disposableManager.add(
            facade.observeGameData()
                .doOnNext { data -> Logger.info { "Game data changed: $data" } }
                .map { data ->
                    when (data) {
                        is DwitchState.RoundIsBeginning -> GameRoomScreen.Dashboard(data.info)
                        is DwitchState.RoundIsOngoing -> GameRoomScreen.Dashboard(data.info)
                        is DwitchState.CardExchange -> initializeCardExchangeScreenState(data.info)
                        DwitchState.CardExchangeOnGoing -> GameRoomScreen.CardExchangeOnGoing
                        is DwitchState.EndOfRound -> GameRoomScreen.EndOfRound(adaptEndOfRoundInfo(data.info))
                    }
                }
                .observeOn(uiScheduler)
                .subscribe { screen -> _screen.value = screen }
        )
    }

    private fun initializeCardExchangeScreenState(cardExchangeInfo: CardExchangeInfo): GameRoomScreen {
        cardExchangeStateEngine = CardExchangeStateEngine(cardExchangeInfo)
        return GameRoomScreen.CardExchange(cardExchangeStateEngine!!.getCardExchangeState())
    }

    private fun adaptEndOfRoundInfo(endOfRoundInfo: EndOfRoundInfo): EndOfRoundInfo {
        val playersSortedByRankASc = endOfRoundInfo.playersInfo.sortedWith { p1, p2 -> -p1.rank.value.compareTo(p2.rank.value) }
        return endOfRoundInfo.copy(playersInfo = playersSortedByRankASc)
    }

    private fun performOperation(successText: String, failureText: String, op: () -> Completable) {
        disposableManager.add(
            op()
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { successText } },
                    { error -> Logger.error(error) { failureText } }
                )
        )
    }
}
