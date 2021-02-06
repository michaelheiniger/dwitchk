package ch.qscqlmpa.dwitchengine.model.player

import kotlinx.serialization.Serializable

@Serializable
sealed class Rank() {

    @Serializable
    object President : Rank()

    @Serializable
    object VicePresident : Rank()

    @Serializable
    object Neutral : Rank()

    @Serializable
    object ViceAsshole : Rank()

    @Serializable
    object Asshole : Rank()
}
