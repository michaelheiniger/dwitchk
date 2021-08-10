package ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.toolbarDefaultTitle
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange.CardExchangeScreenBuilder
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.endofround.EndOfRoundManagerScreenBuilder
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ingame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameAction
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class GameRoomViewModel @Inject constructor(
    private val facade: PlayerFacade,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private lateinit var screenBuilder: GameRoomScreenBuilder

    private val _screen = mutableStateOf<GameRoomScreen>(GameRoomScreen.Loading)
    private val _toolbarTitle = mutableStateOf(toolbarDefaultTitle)
    val screen get(): State<GameRoomScreen> = _screen
    val toolbarTitle get(): State<String> = _toolbarTitle

    init {
        loadGameName()
    }

    fun onCardToPlayClick(cardPlayed: Card) {
        _screen.value = (screenBuilder as DashboardScreenBuilder).onCardClick(cardPlayed)
    }

    fun onPlayClick() {
        idlingResource.increment("Click to play cards")
        val selectedCards = (screenBuilder as DashboardScreenBuilder).selectedCards
        disposableManager.add(
            facade.performAction(GameAction.PlayCard(selectedCards))
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { "Card(s) played successfully ($selectedCards)." } },
                    { error -> Logger.error(error) { "Error while playing card(s)." } }
                )
        )
    }

    fun onPassTurnClick() {
        idlingResource.increment("Click to pass turn")
        disposableManager.add(
            facade.performAction(GameAction.PassTurn)
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { "Turn passed successfully." } },
                    { error -> Logger.error(error) { "Error while passing turn." } }
                )
        )
    }

    fun onCardToExchangeClick(card: Card) {
        _screen.value = (screenBuilder as CardExchangeScreenBuilder).onCardClick(card)
    }

    fun confirmExchange() {
        idlingResource.increment("Click to confirm cards exchange")
        val selectedCards = (screenBuilder as CardExchangeScreenBuilder).selectedCards
        disposableManager.add(
            facade.performAction(GameAction.SubmitCardsForExchange(selectedCards))
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.info { "Cards for exchange submitted successfully ($selectedCards)." } },
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
        idlingResource.increment("Initial screen")
        disposableManager.add(
            facade.observeGameData()
                // Don't want to overwrite the state of the active manager if game data hasn't changed
                // (e.g. selected cards that haven't been submitted yet)
                .distinctUntilChanged()
                .map { state ->
                    when (state) {
                        is DwitchState.RoundIsBeginning -> screenBuilder = DashboardScreenBuilder(state.info)
                        is DwitchState.RoundIsOngoing -> screenBuilder = DashboardScreenBuilder(state.info)
                        is DwitchState.CardExchange -> screenBuilder = CardExchangeScreenBuilder(state.info)
                        DwitchState.CardExchangeOnGoing -> screenBuilder = CardExchangeOnGoingScreenBuilder()
                        is DwitchState.EndOfRound -> screenBuilder = EndOfRoundManagerScreenBuilder(state.info)
                    }
                    screenBuilder.screen
                }
                .doOnNext { screen ->
                    Logger.debug { "New screen emitted: $screen" }
                    idlingResource.decrement("New screen emitted ($screen)")
                }
                .observeOn(uiScheduler)
                .subscribe(
                    { screen -> _screen.value = screen },
                    { error -> Logger.error(error) { "Error while loading game data." } }
                )
        )
    }
}

private class CardExchangeOnGoingScreenBuilder : GameRoomScreenBuilder {
    override val screen: GameRoomScreen get() = GameRoomScreen.CardExchangeOnGoing
}