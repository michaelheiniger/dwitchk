package ch.qscqlmpa.dwitchcommunication

sealed class AddressType {
    data class Unicast(val destination: Address) : AddressType()
    object Broadcast : AddressType()
}