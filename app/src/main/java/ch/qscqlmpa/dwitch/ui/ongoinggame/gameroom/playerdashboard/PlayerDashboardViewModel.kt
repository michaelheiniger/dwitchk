package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.cardexchange.CardExchangeManager
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround.EndOfRoundManager
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class PlayerDashboardViewModel @Inject constructor(
    private val facade: GameFacade,
    private val playManager: PlayManager,
    private val endOfRoundManager: EndOfRoundManager,
    private val cardExchangeManager: CardExchangeManager,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _screen = MutableLiveData<GameRoomScreen>()
    private val _toolbarTitle = MutableLiveData<String>()
    val screen get(): LiveData<GameRoomScreen> = _screen
    val toolbarTitle get(): LiveData<String> = _toolbarTitle

    init {
        loadGameName()
    }

    fun onCardClick(cardPlayed: Card) {
        playManager.onCardClick(cardPlayed)
    }

    fun onPlayClick() {
        disposableManager.add(
            playManager.playCards()
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { "Card(s) played successfully." } },
                    { error -> Logger.error(error) { "Error while playing card(s)." } }
                )
        )
    }

    fun onPassTurnClick() {
        disposableManager.add(
            playManager.passTurn()
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { "Turn passed successfully." } },
                    { error -> Logger.error(error) { "Error while passing turn." } }
                )
        )
    }

    fun onCardToExchangeClick(card: Card) {
        cardExchangeManager.onCardToExchangeClick(card)
    }

    fun confirmExchange() {
        disposableManager.add(
            cardExchangeManager.confirmExchange()
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.info { "Cards for exchange submitted successfully." } },
                    { error -> Logger.error(error) { "Error while submitting cards for exchange." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        observeGameState()
    }

    private fun loadGameName() {
        disposableManager.add(
            facade.getGameName()
                .observeOn(uiScheduler)
                .subscribe(
                    { gameName -> _toolbarTitle.value = gameName },
                    { error -> Logger.error(error) { "Error while loading game name." } }
                )
        )
    }

    private fun observeGameState() {
        disposableManager.add(
            Observable.merge(
                listOf(
                    playManager.observeScreen(),
                    endOfRoundManager.observeScreen(),
                    cardExchangeManager.observeScreen()
                )
            )
                .observeOn(uiScheduler)
                .subscribe(
                    { screen -> _screen.value = screen },
                    { error -> Logger.error(error) { "Error while loading game data." } }
                )
        )
    }
}
