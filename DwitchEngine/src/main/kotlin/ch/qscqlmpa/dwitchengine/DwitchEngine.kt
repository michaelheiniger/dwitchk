package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.startnewgame.GameBootstrap
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import org.tinylog.kotlin.Logger

interface DwitchEngine {
    fun getGameInfo(): DwitchGameInfo
    fun playCard(cardPlayed: Card): DwitchGameState
    fun passTurn(): DwitchGameState
    fun startNewRound(cardDealerFactory: CardDealerFactory): DwitchGameState
    fun getCardExchangeIfRequired(playerId: DwitchPlayerId): DwitchCardExchange?
    fun chooseCardsForExchange(playerId: DwitchPlayerId, cards: Set<Card>): DwitchGameState

    companion object {
        fun createNewGame(players: List<DwitchPlayerOnboardingInfo>, initialGameSetup: InitialGameSetup): DwitchGameState {
            Logger.debug { "Start new game, players:  $players, initial game setup: $initialGameSetup" }
            return GameBootstrap.createNewGame(players, initialGameSetup)
        }
    }
}
