package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchPlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat

class PlayerInfoRobot(private val info: DwitchPlayerInfo) {

    fun assertHand(vararg cards: Card): PlayerInfoRobot {
        assertThat(info.cardsInHand.map(DwitchCardInfo::card)).containsExactlyInAnyOrder(*cards)
        return this
    }

    fun assertHandIsEmpty(): PlayerInfoRobot {
        assertThat(info.cardsInHand).isEmpty()
        return this
    }

    fun assertCanPlay(canPlay: Boolean): PlayerInfoRobot {
        assertThat(info.canPlay).isEqualTo(canPlay)
        return this
    }

    fun assertPlayerStatus(status: DwitchPlayerStatus): PlayerInfoRobot {
        assertThat(info.status).isEqualTo(status)
        return this
    }

    fun assertPlayerRank(rank: DwitchRank): PlayerInfoRobot {
        assertThat(info.rank).isEqualTo(rank)
        return this
    }

    fun assertCanStartNewRound(canStartNewRound: Boolean): PlayerInfoRobot {
        assertThat(info.canStartNewRound).isEqualTo(canStartNewRound)
        return this
    }

    fun assertIsDwitched(): PlayerInfoRobot {
        assertThat(info.dwitched).isTrue
        return this
    }

    fun assertIsNotDwitched(): PlayerInfoRobot {
        assertThat(info.dwitched).isFalse
        return this
    }
}
