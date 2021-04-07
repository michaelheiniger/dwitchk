package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat

class PlayerRobot(private val gameState: DwitchGameState, private val playerId: DwitchPlayerId) {

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

    fun assertCardsForExchangeContainsExactly(vararg expectedCards: Card): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).cardsForExchange).containsExactly(*expectedCards)
        return this
    }

    fun assertPlayerState(expectedStatus: DwitchPlayerStatus): PlayerRobot {
        assertThat(gameState.players.getValue(playerId).status).isEqualTo(expectedStatus)
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

    fun assertRank(rank: DwitchRank): PlayerRobot {
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
