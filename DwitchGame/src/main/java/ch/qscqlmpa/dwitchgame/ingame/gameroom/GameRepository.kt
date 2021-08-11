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
        store.observePlayersInWaitingRoom(),
        store.observeLocalPlayer(),
        store.observeGameState(),
        { players, localPlayer, gameState ->
            when (gameState.phase) {
                DwitchGamePhase.RoundIsBeginning -> DwitchState.RoundIsBeginning(
                    createDashboardInfo(
                        localPlayer = localPlayer,
                        players = players,
                        gameState = gameState
                    )
                )
                DwitchGamePhase.RoundIsOnGoing -> DwitchState.RoundIsOngoing(createDashboardInfo(localPlayer, players, gameState))
                DwitchGamePhase.CardExchange -> getDataForCardExchange(localPlayer, gameState)
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

    private fun createDashboardInfo(localPlayer: Player, players: List<Player>, gameState: DwitchGameState): GameDashboardInfo {
        return GameInfoFactory.createGameDashboardInfo(
            gameInfo = dwitchFactory.createDwitchEngine(gameState).getGameInfo(),
            localPlayerId = localPlayer.dwitchId,
            playersConnected = players.map { p -> p.dwitchId to p.connected }.toMap()
        )
    }

    private fun getDataForCardExchange(localPlayer: Player, gameState: DwitchGameState): DwitchState {
        val cardExchange = dwitchFactory.createDwitchEngine(gameState).getCardExchangeIfRequired(localPlayer.dwitchId)
        return if (cardExchange != null) { // Local player needs to perform a card exchange
            DwitchState.CardExchange(CardExchangeInfo(cardExchange, gameState.player(localPlayer.dwitchId).cardsInHand))
        } else DwitchState.CardExchangeOnGoing
    }

    private fun createEndOfRoundInfo(localPlayer: Player, gameState: DwitchGameState): EndOfRoundInfo {
        return GameInfoFactory.createEndOfGameInfo(
            gameInfo = dwitchFactory.createDwitchEngine(gameState).getGameInfo(),
            localPlayerIsHost = localPlayer.isHost
        )
    }
}
