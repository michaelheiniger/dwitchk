package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.actions.cardexchange.*
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurn
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurnGameUpdater
import ch.qscqlmpa.dwitchengine.actions.passturn.PassTurnState
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCard
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCardGameUpdater
import ch.qscqlmpa.dwitchengine.actions.playcard.PlayCardState
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRound
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRoundGameUpdater
import ch.qscqlmpa.dwitchengine.actions.startnewround.StartNewRoundState
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchGameInfoFactory
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.rules.CardExchangeComputer
import ch.qscqlmpa.dwitchengine.rules.SpecialRule
import org.tinylog.kotlin.Logger

/**
 * Players can play a card if it is either a joker or its value is equal or higher than the last card on the table (no last
 * card on the table means that any card can be played).
 *
 * When a joker is played or when at most one player can play (i.e. a player with status either [DwitchPlayerStatus.Playing] or
 * [DwitchPlayerStatus.Waiting]):
 * - the table is cleared
 * - all active players ([DwitchGameState.activePlayers]) are set to status 'Waiting' except one that is 'Playing'.
 *
 * Invariants when a game is in phase [DwitchGamePhase.RoundIsBeginning] or [DwitchGamePhase.RoundIsOnGoing]:
 * - There must always be exactly one player 'Playing' at all times.
 * - There must always be at least one other player 'Waiting' at all times.
 */
internal class DwitchEngineImpl(private val currentGameState: DwitchGameState) : DwitchEngine {

    private val currentPlayerId = currentGameState.currentPlayerId

    override fun getGameInfo(): DwitchGameInfo {
        return DwitchGameInfoFactory(currentGameState).create()
    }

    override fun playCard(cardPlayed: Card): DwitchGameState {
        Logger.debug { "Player $currentPlayerId plays card $cardPlayed, current game state: $currentGameState" }
        return PlayCard(
            PlayCardState(currentGameState, cardPlayed),
            PlayCardGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    override fun passTurn(): DwitchGameState {
        Logger.debug { "Player $currentPlayerId passes its turn, current game state: $currentGameState" }
        return PassTurn(
            PassTurnState(currentGameState),
            PassTurnGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    override fun startNewRound(cardDealerFactory: CardDealerFactory): DwitchGameState {
        Logger.debug { "Player $currentPlayerId starts a new round, current game state: $currentGameState" }
        return StartNewRound(
            StartNewRoundState(currentGameState),
            StartNewRoundGameUpdater(currentGameState),
            cardDealerFactory
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)
    }

    override fun chooseCardsForExchange(playerId: DwitchPlayerId, cards: Set<Card>): DwitchGameState {
        Logger.debug { "Choose exchange cards $cards for Player $currentPlayerId, current game state: $currentGameState" }
        val gameStateUpdated = CardExchangeChooser(
            CardExchangeChooserState(currentGameState, playerId, cards),
            CardExchangeChooserGameUpdater(currentGameState)
        ).getUpdatedGameState()
            .also(this::logUpdatedGameState)

        val cardExchangePerformer = CardExchangePerformer(
            CardExchangePerformerState(gameStateUpdated),
            CardExchangePerformerGameUpdater(gameStateUpdated)
        )
        if (cardExchangePerformer.cardExchangeReadyToBePerformed()) {
            return cardExchangePerformer
                .getUpdatedGameState()
                .also(this::logUpdatedGameState)
        }
        return gameStateUpdated
    }

    override fun getCardExchangeIfRequired(playerId: DwitchPlayerId): DwitchCardExchange? {
        if (currentGameState.phase != DwitchGamePhase.CardExchange) {
            return null
        }
        val player = currentGameState.player(playerId)

        if (player.cardsForExchange.isEmpty()) {
            return CardExchangeComputer.getCardExchange(player.id, player.rank, player.cardsInHand.toSet())
        }
        return null // Cards already chosen
    }

    override fun isLastCardPlayedTheFirstJackOfTheRound(): Boolean {
        return SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(
            currentGameState.cardsOnTable,
            currentGameState.cardsInGraveyard
        )
    }

    override fun joker(): CardName {
        return currentGameState.joker
    }

    private fun logUpdatedGameState(gameState: DwitchGameState) {
        Logger.debug { "Updated game state: $gameState" }
    }
}
