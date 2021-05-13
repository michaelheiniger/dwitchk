package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.CardInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.DashboardInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.LocalPlayerInfo
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.DwitchCardInfoValueDescComparator
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameAction
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameDashboardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class PlayManager @Inject constructor(private val facade: GameFacade) {
    private val disposableManager = DisposableManager()
    private val relay = PublishRelay.create<GameRoomScreen.Dashboard>()

    private lateinit var dashboardInfo: DashboardInfo
    private var playCardEngine: PlayCardEngine? = null

    init {
        disposableManager.add(
            facade.observeGameData()
                .subscribe { data ->
                    when (data) {
                        is DwitchState.RoundIsBeginning -> setupAndEmitInitialScreen(data.info)
                        is DwitchState.RoundIsOngoing -> setupAndEmitInitialScreen(data.info)
                        else -> {
                            // Nothing to do
                        }
                    }
                }
        )
    }

    fun observeScreen(): Observable<GameRoomScreen.Dashboard> {
        return relay.doOnDispose { disposableManager.disposeAndReset() }
    }

    fun onCardClick(cardPlayed: Card) {
        Logger.debug { "Click on card $cardPlayed" }
        playCardEngine!!.onCardClick(cardPlayed)
        val localPlayerInfo = dashboardInfo.localPlayerInfo.copy(
            cardsInHand = playCardEngine!!.getCardsInHand(),
            canPlay = playCardEngine!!.cardSelectionIsValid()
        )
        dashboardInfo = dashboardInfo.copy(localPlayerInfo = localPlayerInfo)
        relay.accept(GameRoomScreen.Dashboard(dashboardInfo))
    }

    fun playCards(): Completable {
        val cardsToPlay = playCardEngine!!.getSelectedCards()
        Logger.debug { "Playing card(s) $cardsToPlay" }
        return facade.performAction(GameAction.PlayCard(cardsToPlay))
    }

    fun passTurn(): Completable {
        Logger.debug { "Passing turn" }
        return facade.performAction(GameAction.PassTurn)
    }

    private fun setupAndEmitInitialScreen(info: GameDashboardInfo) {
        val sortedCardsInHand = info.localPlayerDashboard.cardsInHand.sortedWith(DwitchCardInfoValueDescComparator())
        playCardEngine = PlayCardEngine(
            sortedCardsInHand.map { c -> CardInfo(c.card, c.selectable) },
            info.lastCardPlayed
        )
        dashboardInfo = DashboardInfo(
            playersInfo = info.playersInfo,
            localPlayerInfo = LocalPlayerInfo(
                cardsInHand = playCardEngine!!.getCardsInHand(),
                canPlay = playCardEngine!!.cardSelectionIsValid(),
                canPass = info.localPlayerDashboard.canPass
            ),
            lastCardPlayed = info.lastCardPlayed
        )
        relay.accept(GameRoomScreen.Dashboard(dashboardInfo))
    }
}