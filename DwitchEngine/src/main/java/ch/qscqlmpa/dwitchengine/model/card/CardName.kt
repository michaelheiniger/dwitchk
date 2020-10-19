package ch.qscqlmpa.dwitchengine.model.card

import kotlinx.serialization.Serializable

@Serializable
sealed class CardName(val name: String, val value: Int) {

    @Serializable
    object Two : CardName("Two", 2)

    @Serializable
    object Three : CardName("Three", 3)

    @Serializable
    object Four : CardName("Four", 4)

    @Serializable
    object Five : CardName("Five", 5)

    @Serializable
    object Six : CardName("Six", 6)

    @Serializable
    object Seven : CardName("Seven", 7)

    @Serializable
    object Eight : CardName("Eight", 8)

    @Serializable
    object Nine : CardName("Nine", 9)

    @Serializable
    object Ten : CardName("Ten", 10)

    @Serializable
    object Jack : CardName("Jack", 11)

    @Serializable
    object Queen : CardName("Queen", 12)

    @Serializable
    object King : CardName("King", 13)

    @Serializable
    object Ace : CardName("Ace", 14)

    @Serializable
    object Blank : CardName("Blank", 0)
}