package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket

sealed class AddressType {
    data class Unicast(val destination: Address) : AddressType()
    object Broadcast : AddressType()
}