package ch.qscqlmpa.dwitchcommunication.ingame

internal sealed class AddressType {
    data class Unicast(val destination: Address) : AddressType()
    object Broadcast : AddressType()
}
