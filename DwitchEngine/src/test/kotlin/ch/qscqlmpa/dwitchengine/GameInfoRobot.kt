package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import org.assertj.core.api.Assertions.assertThat

class GameInfoRobot(private val info: GameInfo) {

    fun assertGameEvent(gameEvent: GameEvent?): GameInfoRobot {
        assertThat(info.gameEvent).isEqualTo(gameEvent)
        return this
    }

    fun assertGamePhase(gamePhase: GamePhase): GameInfoRobot {
        assertThat(info.gamePhase).isEqualTo(gamePhase)
        return this
    }

    fun assertJoker(card: CardName): GameInfoRobot {
        assertThat(info.joker).isEqualTo(card)
        return this
    }

    fun assertCardsOnTable(cards: List<Card>): GameInfoRobot {
        assertThat(info.cardsOnTable).isEqualTo(cards)
        return this
    }

    fun assertTableEmpty(): GameInfoRobot {
        assertThat(info.cardsOnTable).isEmpty()
        return this
    }

    fun assertPlayingOrder(vararg playerIds: PlayerDwitchId): GameInfoRobot {
        assertThat(info.playingOrder).containsExactly(*playerIds)
        return this
    }
}
