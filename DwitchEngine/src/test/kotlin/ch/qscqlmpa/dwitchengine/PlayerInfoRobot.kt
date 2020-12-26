package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat

class PlayerInfoRobot(private val info: PlayerInfo) {

    fun assertCanPlay(canPlay: Boolean): PlayerInfoRobot {
        assertThat(info.canPlay).isEqualTo(canPlay)
        return this
    }

    fun assertCanPickACard(canPickACard: Boolean): PlayerInfoRobot {
        assertThat(info.canPickACard).isEqualTo(canPickACard)
        return this
    }

    fun assertCanPass(canPass: Boolean): PlayerInfoRobot {
        assertThat(info.canPass).isEqualTo(canPass)
        return this
    }

    fun assertCardsInHandInAnyOrder(vararg cards: Card): PlayerInfoRobot {
        assertThat(info.cardsInHand).containsExactlyInAnyOrder(*cards)
        return this
    }

    fun assertMinimumCardValueAllowed(value: CardName): PlayerInfoRobot {
        assertThat(info.minimumPlayingCardValueAllowed).isEqualTo(value)
        return this
    }

    fun assertPlayerStatus(status: PlayerStatus): PlayerInfoRobot {
        assertThat(info.status).isEqualTo(status)
        return this
    }

    fun assertPlayerRank(rank: Rank): PlayerInfoRobot {
        assertThat(info.rank).isEqualTo(rank)
        return this
    }

    fun assertCanStartNewRound(canStartNewRound: Boolean): PlayerInfoRobot {
        assertThat(info.canStartNewRound).isEqualTo(canStartNewRound)
        return this
    }

    fun assertCanEndGame(canEndGame: Boolean): PlayerInfoRobot {
        assertThat(info.canEndGame).isEqualTo(canEndGame)
        return this
    }

    fun assertDwitched(): PlayerInfoRobot {
        assertThat(info.dwitched).isTrue
        return this
    }
}