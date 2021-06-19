package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchengine.DwitchFactory
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@OngoingGameScope
internal class GameRepository @Inject constructor(
    private val store: InGameStore,
    private val dwitchFactory: DwitchFactory
) {
    private val gameData = Observable.combineLatest(
        store.observeLocalPlayer().distinctUntilChanged(),
        store.observeGameState().distinctUntilChanged(),
        { localPlayer, gameState ->
            when (gameState.phase) {
                DwitchGamePhase.RoundIsBeginning -> DwitchState.RoundIsBeginning(createDashboardInfo(localPlayer, gameState))
                DwitchGamePhase.CardExchange -> getDataForCardExchange(localPlayer, gameState)
                DwitchGamePhase.RoundIsOnGoing -> DwitchState.RoundIsOngoing(createDashboardInfo(localPlayer, gameState))
                DwitchGamePhase.RoundIsOver -> DwitchState.EndOfRound(createEndOfRoundInfo(localPlayer, gameState))
            }
        }
    )

    fun getGameName(): Single<String> {
        return Single.fromCallable { store.getGameName() }
    }

    fun observeGameData(): Observable<DwitchState> {
        return gameData.doOnNext { data -> Logger.debug { "GameData emitted: $data" } }
    }

    private fun getDataForCardExchange(localPlayer: Player, gameState: DwitchGameState): DwitchState {
        val cardExchange = dwitchFactory.createDwitchEngine(gameState).getCardExchangeIfRequired(localPlayer.dwitchId)
        if (cardExchange != null) { // Local player needs to perform a card exchange
            return DwitchState.CardExchange(CardExchangeInfo(cardExchange, gameState.player(localPlayer.dwitchId).cardsInHand))
        }
        return DwitchState.CardExchangeOnGoing
    }

    private fun createDashboardInfo(localPlayer: Player, gameState: DwitchGameState): GameDashboardInfo {
        return GameInfoFactory.createGameDashboardInfo(
            dwitchFactory.createDwitchEngine(gameState).getGameInfo(),
            localPlayer.dwitchId,
            localPlayer.connected
        )
    }

    private fun createEndOfRoundInfo(localPlayer: Player, gameState: DwitchGameState): EndOfRoundInfo {
        val playerInfos = dwitchFactory.createDwitchEngine(gameState).getGameInfo().playerInfos.values.toList()
        return GameInfoFactory.createEndOfGameInfo(playerInfos, localPlayerIsHost = localPlayer.isHost)
    }
}
