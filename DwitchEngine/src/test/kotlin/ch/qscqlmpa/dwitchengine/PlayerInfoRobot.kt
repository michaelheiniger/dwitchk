package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.info.DwitchPlayerInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat

class PlayerInfoRobot(private val info: DwitchPlayerInfo) {

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

    fun assertDwitched(): PlayerInfoRobot {
        assertThat(info.dwitched).isTrue
        return this
    }
}
