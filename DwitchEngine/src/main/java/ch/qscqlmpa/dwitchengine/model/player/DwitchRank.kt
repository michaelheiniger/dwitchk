package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class DwitchRank(val value: Int) {

    @Serializable
    object President : DwitchRank(1)

    @Serializable
    object VicePresident : DwitchRank(2)

    @Serializable
    object Neutral : DwitchRank(3)

    @Serializable
    object ViceAsshole : DwitchRank(4)

    @Serializable
    object Asshole : DwitchRank(5)
}
