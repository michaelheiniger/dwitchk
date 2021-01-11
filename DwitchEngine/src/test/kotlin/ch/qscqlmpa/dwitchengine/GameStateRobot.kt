package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker
import org.assertj.core.api.Assertions.assertThat

class GameStateRobot(private val gameState: GameState) {

    fun assertNumCardsOnTable(expectedValue: Int): GameStateRobot {
        assertThat(gameState.cardsOnTable.size).isEqualTo(expectedValue)
        return this
    }

    fun assertCardsOnTableContains(vararg expectedCards: Card): GameStateRobot {
        assertThat(gameState.cardsOnTable).contains(*expectedCards)
        return this
    }

    fun assertCardsOnTableContainsExactly(vararg expectedCards: Card): GameStateRobot {
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

    fun assertActivePlayers(vararg playersId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.activePlayers).containsExactly(*playersId)
        return this
    }

    fun assertCurrentPlayerId(expectedPlayerId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.currentPlayerId).isEqualTo(expectedPlayerId)
        return this
    }

    fun assertPlayersDoneForRoundIsEmpty(): GameStateRobot {
        assertThat(gameState.playersDoneForRound.isEmpty()).isTrue
        return this
    }

    fun assertPlayerIsDoneForRound(playerId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.playersDoneForRound).contains(playerId)
        return this
    }

    fun assertPlayerHasFinishedWithJoker(playerId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).contains(SpecialRuleBreaker.FinishWithJoker(playerId))
        return this
    }

    fun assertPlayerHasNotFinishedWithJoker(playerId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).noneMatch { p -> p == SpecialRuleBreaker.FinishWithJoker(playerId)}
        return this
    }

    fun assertPlayerHasBrokenFirstJackPlayedRule(playerId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).contains(SpecialRuleBreaker.PlayedOnFirstJack(playerId))
        return this
    }

    fun assertPlayerHasNotBrokenFirstJackPlayedRule(playerId: PlayerDwitchId): GameStateRobot {
        assertThat(gameState.playersWhoBrokeASpecialRule).noneMatch { p -> p == SpecialRuleBreaker.PlayedOnFirstJack(playerId)}
        return this
    }

    fun assertTableIsCleared(): GameStateRobot {
        assertThat(gameState.cardsOnTable).isEqualTo(emptyList<Card>())
        return this
    }

    fun assertTableContains(card: Card): GameStateRobot {
        assertThat(gameState.cardsOnTable).contains(card)
        return this
    }

    fun assertRoundIsOver(): GameStateRobot {
        assertThat(gameState.phase).isEqualTo(GamePhase.RoundIsOver)
        return this
    }

    fun assertRoundIsNotOver(): GameStateRobot {
        assertThat(gameState.phase).isNotEqualTo(GamePhase.RoundIsOver)
        return this
    }

    fun assertGameEvent(gameEvent: GameEvent?): GameStateRobot {
        assertThat(gameState.gameEvent).isEqualTo(gameEvent)
        return this
    }

    fun assertGamePhase(phase: GamePhase): GameStateRobot {
        assertThat(gameState.phase).isEqualTo(phase)
        return this
    }

    fun assertPlayingOrder(playingOrder: List<PlayerDwitchId>): GameStateRobot {
        assertThat(gameState.playingOrder).isEqualTo(playingOrder)
        return this
    }

    fun assertJoker(cardName: CardName): GameStateRobot {
        assertThat(gameState.joker).isEqualTo(cardName)
        return this
    }
}