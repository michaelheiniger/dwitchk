package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat

class PlayerRobot(private val gameState: GameState, private val playerId: PlayerInGameId) {

    fun assertNumCardsInHand(expectedValue: Int): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).cardsInHand.size).isEqualTo(expectedValue)
        return this
    }

    fun assertCardsInHandContains(vararg expectedCards: Card): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).cardsInHand).contains(*expectedCards)
        return this
    }

    fun assertCardsInHandContainsExactly(vararg expectedCards: Card): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).cardsInHand).containsExactly(*expectedCards)
        return this
    }

    fun assertPlayerState(expectedState: PlayerState): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).state).isEqualTo(expectedState)
        return this
    }

    fun assertPlayerIsDwitched(): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).dwitched).isTrue()
        return this
    }

    fun assertPlayerIsNotDwitched(): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).dwitched).isFalse()
        return this
    }

    fun assertRank(rank: Rank): PlayerRobot {
        assertThat(gameState.player(playerId).rank).isEqualTo(rank)
        return this
    }

    fun assertPlayerHasPickedCard(): PlayerRobot {
        assertThat(gameState.player(playerId).hasPickedACard).isTrue()
        return this
    }

    fun assertPlayerHasNotPickedCard(): PlayerRobot {
        assertThat(gameState.player(playerId).hasPickedACard).isFalse()
        return this
    }
}