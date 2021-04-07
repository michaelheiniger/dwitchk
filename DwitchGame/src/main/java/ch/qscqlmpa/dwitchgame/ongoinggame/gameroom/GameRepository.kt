package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchengine.DwitchEngineFactory
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@OngoingGameScope
internal class GameRepository @Inject constructor(
    private val store: InGameStore,
    private val dwitchEngineFactory: DwitchEngineFactory
) {

    fun observeGameData(): Observable<DwitchState> {
        return Observable.combineLatest(
            store.observeLocalPlayer(),
            store.observeGameState(),
            { localPlayer, gameState ->
                when (gameState.phase) {
                    GamePhase.RoundIsBeginning -> DwitchState.RoundIsBeginning(createDashboardInfo(localPlayer, gameState))
                    GamePhase.CardExchange -> getDataForCardExchange(localPlayer, gameState)
                    GamePhase.RoundIsOnGoing -> DwitchState.RoundIsOngoing(createDashboardInfo(localPlayer, gameState))
                    GamePhase.RoundIsOver -> DwitchState.EndOfRound(createEndOfRoundInfo(localPlayer, gameState))
                    else -> throw IllegalStateException("Unexpected error") // To satisfy buggy compiler
                }
            }
        ).distinctUntilChanged()
    }

    private fun getDataForCardExchange(localPlayer: Player, gameState: GameState): DwitchState {
        val cardExchange = dwitchEngineFactory.create(gameState).getCardExchangeIfRequired(localPlayer.dwitchId)
        if (cardExchange != null) { // Local player needs to perform a card exchange
            return DwitchState.CardExchange(CardExchangeInfo(cardExchange, gameState.player(localPlayer.dwitchId).cardsInHand))
        }
        return DwitchState.CardExchangeOnGoing
    }

    private fun createDashboardInfo(localPlayer: Player, gameState: GameState): GameDashboardInfo {
        return GameInfoFactory.createGameDashboardInfo(
            dwitchEngineFactory.create(gameState).getGameInfo(),
            localPlayer.dwitchId,
            localPlayer.connectionState
        )
    }

    private fun createEndOfRoundInfo(localPlayer: Player, gameState: GameState): EndOfRoundInfo {
        val playerInfos = dwitchEngineFactory.create(gameState).getGameInfo().playerInfos.values.toList()
        return GameInfoFactory.createEndOfGameInfo(playerInfos, localPlayerIsHost = localPlayer.isHost)
    }
}