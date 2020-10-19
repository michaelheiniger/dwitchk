package ch.qscqlmpa.dwitch.communication

sealed class AddressType {
    data class Unicast(val destination: Address) : AddressType()
    object Broadcast : AddressType()
}