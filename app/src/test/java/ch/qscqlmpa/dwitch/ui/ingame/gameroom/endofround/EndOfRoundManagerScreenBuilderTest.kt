package ch.qscqlmpa.dwitch.ui.ingame.gameroom.endofround

import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ingame.gameroom.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerEndOfRoundInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class EndOfRoundManagerScreenBuilderTest {

    private lateinit var endOfRoundManagerScreenBuilder: EndOfRoundManagerScreenBuilder

    private val initialEndOfRoundInfo = EndOfRoundInfo(
        playersInfo = listOf(
            PlayerEndOfRoundInfo("Aragorn", DwitchRank.Asshole),
            PlayerEndOfRoundInfo("Haldir", DwitchRank.President)
        ),
        canStartNewRound = true,
        canEndGame = true
    )

    @Before
    fun setup() {
        endOfRoundManagerScreenBuilder = EndOfRoundManagerScreenBuilder(initialEndOfRoundInfo)
    }

    @Test
    fun `Initial screen is generated according to end of round info`() {
        // Given initial end of round info
        // Then the screen is properly built
        val endOfRoundInfo = endOfRoundManagerScreenBuilder.screen.endOfRoundInfo
        assertThat(endOfRoundInfo.canStartNewRound).isTrue
        assertThat(endOfRoundInfo.canEndGame).isTrue
        assertThat(endOfRoundInfo.playersInfo).containsExactlyInAnyOrder(
            PlayerEndOfRoundInfo("Aragorn", DwitchRank.Asshole),
            PlayerEndOfRoundInfo("Haldir", DwitchRank.President)
        )
    }

    @Test
    fun `Players are sorted by rank desc`() {
        // Given unsorted players
        assertThat(initialEndOfRoundInfo.playersInfo[0].name).isEqualTo("Aragorn")
        assertThat(initialEndOfRoundInfo.playersInfo[1].name).isEqualTo("Haldir")

        // When querying the screen, then the players are sorted
        val players = endOfRoundManagerScreenBuilder.screen.endOfRoundInfo.playersInfo

        // Then the players are sorted
        assertThat(players.size).isEqualTo(2)
        assertThat(players[0].name).isEqualTo("Haldir")
        assertThat(players[1].name).isEqualTo("Aragorn")
    }
}
