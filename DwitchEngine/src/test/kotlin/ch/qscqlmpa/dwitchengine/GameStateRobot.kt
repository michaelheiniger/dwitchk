package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.DwitchPlayerAction
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker
import org.assertj.core.api.Assertions.assertThat

class GameStateRobot(private val gameState: DwitchGameState) {

    fun assertNumCardsOnTable(expectedValue: Int): GameStateRobot {
        assertThat(gameState.cardsOnTable.size).isEqualTo(expectedValue)
        return this
    }

    fun assertCardsOnTableContains(vararg expectedCards: PlayedCards): GameStateRobot {
        assertThat(gameState.cardsOnTable).contains(*expectedCards)
        return this
    }

    fun assertCardsOnTable(vararg expectedCards: PlayedCards): GameStateRobot {
        assertThat(gameState.cardsOnTable).containsExactly(*expectedCards)
        return this
    }

    fun assertNumCardsInDeck(expectedValue: Int): GameStateRobot {
        assertThat(gameState.cardsInDeck.size).isEqualTo(expectedValue)
        return this
    }

    fun assertNumCardsInGraveyard(expectedValue: Int): GameStateRobot {
        assertThat(gameState.cardsInGraveyard.size).isEqualTo(expectedValue)
        return this
    }

    fun assertActivePlayers(vararg playersId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.activePlayers).containsExactly(*playersId)
        return this
    }

    fun assertCurrentPlayerId(expectedPlayerId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.currentPlayerId).isEqualTo(expectedPlayerId)
        return this
    }

    fun assertPlayersDoneForRoundIsEmpty(): GameStateRobot {
        assertThat(gameState.playersDoneForRound.isEmpty()).isTrue
        return this
    }

    fun assertPlayerIsDoneForRound(playerId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.playersDoneForRound).contains(playerId)
        return this
    }

    fun assertPlayerHasFinishedWithJoker(playerId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).contains(SpecialRuleBreaker.FinishWithJoker(playerId))
        return this
    }

    fun assertPlayerHasNotFinishedWithJoker(playerId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).noneMatch { p -> p == SpecialRuleBreaker.FinishWithJoker(playerId) }
        return this
    }

    fun assertPlayerHasBrokenFirstJackPlayedRule(playerId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).contains(SpecialRuleBreaker.PlayedOnFirstJack(playerId))
        return this
    }

    fun assertPlayerHasNotBrokenFirstJackPlayedRule(playerId: DwitchPlayerId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).noneMatch { p -> p == SpecialRuleBreaker.PlayedOnFirstJack(playerId) }
        return this
    }

    fun assertTableIsEmpty(): GameStateRobot {
        assertThat(gameState.cardsOnTable).isEmpty()
        return this
    }

    fun assertTableContains(cards: PlayedCards): GameStateRobot {
        assertThat(gameState.cardsOnTable).contains(cards)
        return this
    }

    fun assertRoundIsOver(): GameStateRobot {
        assertThat(gameState.phase).isEqualTo(DwitchGamePhase.RoundIsOver)
        return this
    }

    fun assertRoundIsNotOver(): GameStateRobot {
        assertThat(gameState.phase).isNotEqualTo(DwitchGamePhase.RoundIsOver)
        return this
    }

    fun assertLastPlayerAction(action: DwitchPlayerAction?): GameStateRobot {
        assertThat(gameState.lastPlayerAction).isEqualTo(action)
        return this
    }

    fun assertGamePhase(phase: DwitchGamePhase): GameStateRobot {
        assertThat(gameState.phase).isEqualTo(phase)
        return this
    }

    fun assertPlayingOrder(playingOrder: List<DwitchPlayerId>): GameStateRobot {
        assertThat(gameState.playingOrder).isEqualTo(playingOrder)
        return this
    }

    fun assertJoker(cardName: CardName): GameStateRobot {
        assertThat(gameState.joker).isEqualTo(cardName)
        return this
    }
}
