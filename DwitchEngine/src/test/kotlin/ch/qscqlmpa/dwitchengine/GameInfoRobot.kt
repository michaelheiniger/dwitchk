package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import org.assertj.core.api.Assertions.assertThat

class GameInfoRobot(private val info: DwitchGameInfo) {

    fun assertGameEvent(dwitchGameEvent: DwitchGameEvent?): GameInfoRobot {
        assertThat(info.dwitchGameEvent).isEqualTo(dwitchGameEvent)
        return this
    }

    fun assertGamePhase(gamePhase: DwitchGamePhase): GameInfoRobot {
        assertThat(info.gamePhase).isEqualTo(gamePhase)
        return this
    }

    fun assertJoker(card: CardName): GameInfoRobot {
        assertThat(info.joker).isEqualTo(card)
        return this
    }

    fun assertCardsOnTable(vararg cards: Card): GameInfoRobot {
        assertThat(info.cardsOnTable).containsExactly(*cards)
        return this
    }

    fun assertTableIsEmpty(): GameInfoRobot {
        assertThat(info.cardsOnTable).isEmpty()
        return this
    }

    fun assertPlayingOrder(vararg playerIds: DwitchPlayerId): GameInfoRobot {
        assertThat(info.playingOrder).containsExactly(*playerIds)
        return this
    }
}
