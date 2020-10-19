package ch.qscqlmpa.dwitchengine.model.card

import kotlinx.serialization.Serializable

@Serializable
sealed class CardSuit(val name: String) {

    @Serializable
    object Clubs : CardSuit("Clubs")

    @Serializable
    object Diamonds : CardSuit("Diamonds")

    @Serializable
    object Spades : CardSuit("Spades")

    @Serializable
    object Hearts : CardSuit("Hearts")

    @Serializable
    object Blank : CardSuit("Blank")
}