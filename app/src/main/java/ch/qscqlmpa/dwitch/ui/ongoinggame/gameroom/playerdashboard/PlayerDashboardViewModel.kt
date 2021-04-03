package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class PlayerDashboardViewModel @Inject constructor(
    private val facade: GameDashboardFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<PlayerDashboardCommand>()
    private val _gameDashboardInfo = MutableLiveData<GameDashboardInfo>()

    val commands get(): LiveData<PlayerDashboardCommand> = _commands
    val gameDashboardInfo get(): LiveData<GameDashboardInfo> = _gameDashboardInfo

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

    override fun onStart() {
        super.onStart()
        observePlayerDashboard()
        observeEndOfRoundEvents()
        observeCardExchange()
    }

    override fun onStop() {
        super.onStop()
        disposableManager.disposeAndReset()
    }

    private fun observePlayerDashboard() {
        disposableManager.add(
            facade.observeDashboard()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing player dashboard." } }
                .subscribe { dashboard -> _gameDashboardInfo.value = dashboard }
        )
    }

    private fun observeEndOfRoundEvents() {
        disposableManager.add(
            facade.observeEndOfRound()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing end of round events." } }
                .subscribe { _commands.value = PlayerDashboardCommand.OpenEndOfRoundInfo }
        )
    }

    private fun observeCardExchange() {
        disposableManager.add(
            facade.observeCardExchangeEvents()
                .observeOn(uiScheduler)
                .doOnError { error -> Logger.error(error) { "Error while observing card exchange events." } }
                .subscribe { _commands.value = PlayerDashboardCommand.OpenCardExchange }
        )
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
