package ch.qscqlmpa.dwitch.ui

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchengine.model.player.Rank

object EntityTextMapper {

    fun rankText(rank: Rank): Int {
        return when (rank) {
            Rank.President -> R.string.president
            Rank.VicePresident -> R.string.vice_president
            Rank.Neutral -> R.string.neutral
            Rank.ViceAsshole -> R.string.vice_asshole
            Rank.Asshole -> R.string.asshole
        }
    }
}





