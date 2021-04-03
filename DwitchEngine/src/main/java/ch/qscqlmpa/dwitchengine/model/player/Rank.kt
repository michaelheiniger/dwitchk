package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class Rank(val value: Int) {

    @Serializable
    object President : Rank(1)

    @Serializable
    object VicePresident : Rank(2)

    @Serializable
    object Neutral : Rank(3)

    @Serializable
    object ViceAsshole : Rank(4)

    @Serializable
    object Asshole : Rank(5)
}
