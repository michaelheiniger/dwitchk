package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerOnboardingInfo
import org.tinylog.kotlin.Logger

interface DwitchEngine {
    fun getGameInfo(): GameInfo
    fun playCard(cardPlayed: Card): GameState
    fun pickCard(): GameState
    fun passTurn(): GameState
    fun startNewRound(cardDealerFactory: CardDealerFactory): GameState
    fun chooseCardsForExchange(playerId: PlayerDwitchId, cards: Set<Card>): GameState
    fun getCardsExchange(playerId: PlayerDwitchId): CardExchange?

    companion object {
        fun createNewGame(playersInfo: List<PlayerOnboardingInfo>, initialGameSetup: InitialGameSetup): GameState {
            Logger.debug { "Start new game, players:  $playersInfo, initial game setup: $initialGameSetup" }
            return GameBootstrap.createNewGame(playersInfo, initialGameSetup)
        }
    }
}
